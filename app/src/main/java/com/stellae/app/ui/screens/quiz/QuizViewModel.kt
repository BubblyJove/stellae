package com.stellae.app.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stellae.app.domain.gamification.RankSystem
import com.stellae.app.domain.model.QuizQuestion
import com.stellae.app.domain.model.Rank
import com.stellae.app.domain.model.Rating
import com.stellae.app.domain.model.SessionResult
import com.stellae.app.domain.model.FsrsState
import com.stellae.app.domain.repository.CardRepository
import com.stellae.app.domain.repository.UserRepository
import com.stellae.app.domain.usecase.GetDueCardsUseCase
import com.stellae.app.domain.usecase.GetNextQuestionUseCase
import com.stellae.app.domain.usecase.RecordAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getDueCardsUseCase: GetDueCardsUseCase,
    private val getNextQuestionUseCase: GetNextQuestionUseCase,
    private val recordAnswerUseCase: RecordAnswerUseCase,
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository,
    private val rankSystem: RankSystem,
) : ViewModel() {

    data class QuizUiState(
        val currentQuestion: QuizQuestion? = null,
        val questionNumber: Int            = 0,
        val totalQuestions: Int            = 0,
        val xpEarned: Long                 = 0L,
        val sessionProgress: Float         = 0f,
        val selectedAnswer: Int?           = null,
        val isCorrect: Boolean?            = null,
        val feedbackText: String           = "",
        val feedbackExplanation: String    = "",
        val isSessionComplete: Boolean     = false,
        val sessionStartTime: Long         = 0L,
        val isLoading: Boolean             = true,
    )

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    // Internal session state
    private var cardQueue: List<FsrsState> = emptyList()
    private var currentIndex: Int = 0
    private var correctCount: Int = 0
    private var questionStartTimeMs: Long = 0L

    // Snapshot of rank / XP at session start for SessionResult
    private var rankBefore: Rank = Rank.NEOPHYTE
    private var xpBefore: Long = 0L
    private var streakCount: Int = 0
    private var streakLastDate: String = ""

    init {
        loadSession()
    }

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Record the user's answer choice and compute feedback.
     *
     * Transitions the UI from "question showing" to "feedback showing" state.
     * The next question is not loaded yet — call [nextQuestion] for that.
     */
    fun selectAnswer(index: Int) {
        val state    = _uiState.value
        val question = state.currentQuestion ?: return
        // Ignore taps after an answer has already been selected.
        if (state.selectedAnswer != null) return

        val responseMs = System.currentTimeMillis() - questionStartTimeMs
        val isCorrect  = (index == question.correctIndex)
        val rating     = if (isCorrect) Rating.GOOD else Rating.AGAIN

        if (isCorrect) correctCount++

        viewModelScope.launch {
            val answerResult = recordAnswerUseCase(
                cardId         = question.card.id,
                rating         = rating,
                chosenAnswer   = question.options.getOrNull(index),
                correctAnswer  = question.card.correctAnswer,
                responseTimeMs = responseMs,
                streakCount    = streakCount,
            )

            val feedbackText = if (isCorrect) {
                "Correct! +${answerResult.xpEarned} XP"
            } else {
                "Not yet mastered"
            }

            _uiState.update { current ->
                current.copy(
                    selectedAnswer      = index,
                    isCorrect           = isCorrect,
                    feedbackText        = feedbackText,
                    feedbackExplanation = answerResult.explanation,
                    xpEarned            = current.xpEarned + answerResult.xpEarned,
                )
            }
        }
    }

    /**
     * Advance to the next card in the queue.
     *
     * If the queue is exhausted the session is marked complete, which
     * triggers navigation to the Summary screen.
     */
    fun nextQuestion() {
        currentIndex++

        if (currentIndex >= cardQueue.size) {
            completeSession()
            return
        }

        loadQuestion(currentIndex)
    }

    /** Build the [SessionResult] to hand off to the Summary screen. */
    fun getSessionResult(): SessionResult {
        val xpAfter      = xpBefore + _uiState.value.xpEarned
        val rankAfter    = rankSystem.getCurrentRank(xpAfter)
        val durationSecs = ((System.currentTimeMillis() - _uiState.value.sessionStartTime) / 1000L)
            .coerceAtLeast(1L)
            .toInt()

        val accuracy = if (cardQueue.isNotEmpty()) {
            correctCount.toFloat() / cardQueue.size.toFloat()
        } else 0f

        return SessionResult(
            cardsReviewed    = cardQueue.size,
            correctCount     = correctCount,
            xpEarned         = _uiState.value.xpEarned,
            durationSeconds  = durationSecs,
            streakMaintained = true,
            newStreakCount    = streakCount,
            rankBefore       = rankBefore,
            rankAfter        = rankAfter,
            xpBefore         = xpBefore,
            xpAfter          = xpAfter,
            weakAreas        = emptyList(),
            tip              = buildTip(accuracy),
        )
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun loadSession() {
        viewModelScope.launch {
            val now = System.currentTimeMillis()

            // Set session start time immediately so the duration is always valid.
            _uiState.update { it.copy(sessionStartTime = now) }

            // Snapshot rank / XP before the session mutates them.
            val progress = userRepository.getUserProgress().first()
            rankBefore    = progress.rank
            xpBefore      = progress.xp
            streakCount   = progress.streakCount
            streakLastDate = progress.streakLastDate

            cardQueue = getDueCardsUseCase()

            if (cardQueue.isEmpty()) {
                _uiState.update { it.copy(isSessionComplete = true, isLoading = false) }
                return@launch
            }

            _uiState.update { it.copy(
                totalQuestions   = cardQueue.size,
                isLoading        = false,
            )}

            loadQuestion(0)
        }
    }

    private fun loadQuestion(index: Int) {
        viewModelScope.launch {
            val question = getNextQuestionUseCase(cardQueue, index)

            questionStartTimeMs = System.currentTimeMillis()

            val progress = if (cardQueue.isNotEmpty()) {
                index.toFloat() / cardQueue.size.toFloat()
            } else 0f

            _uiState.update { current ->
                current.copy(
                    currentQuestion     = question,
                    questionNumber      = index + 1,
                    sessionProgress     = progress,
                    selectedAnswer      = null,
                    isCorrect           = null,
                    feedbackText        = "",
                    feedbackExplanation = "",
                )
            }
        }
    }

    private fun completeSession() {
        val xpAfter = xpBefore + _uiState.value.xpEarned

        viewModelScope.launch {
            // Persist the session log so TodayStats and history are up to date.
            userRepository.logSession(getSessionResult())

            // Update the streak based on today's practice.
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            userRepository.updateStreak(streakCount + 1, today)
        }

        _uiState.update { it.copy(
            sessionProgress  = 1f,
            isSessionComplete = true,
        )}
    }

    private fun buildTip(accuracy: Float): String = when {
        accuracy >= 0.9f -> "Excellent work! Keep up the daily practice to maintain your streak."
        accuracy >= 0.7f -> "Good session. Review the cards you missed to strengthen weak spots."
        else             -> "Focus on the dignity categories with the most errors first."
    }
}
