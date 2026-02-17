package com.stellae.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stellae.app.domain.gamification.RankSystem
import com.stellae.app.domain.gamification.StreakManager
import com.stellae.app.domain.repository.CardRepository
import com.stellae.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val cardRepository: CardRepository,
    private val rankSystem: RankSystem,
    private val streakManager: StreakManager,
) : ViewModel() {

    data class HomeUiState(
        val rankTitle: String       = "Neophyte",
        val xpCurrent: Long         = 0L,
        val xpForNext: Long         = 500L,
        val rankProgress: Float     = 0f,
        val streakCount: Int        = 0,
        val dueReviewCount: Int     = 0,
        val accuracy: Float         = 0f,
        val newCardCount: Int       = 0,
        val dailyMission: String    = "Start your first session",
        val onboardingComplete: Boolean = false,
        val isLoading: Boolean      = true,
    )

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeProgressAndDueCount()
        loadTodayStats()
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Combine the user progress flow and the due-card count flow into a single
     * state update so the UI always sees a consistent snapshot.
     */
    private fun observeProgressAndDueCount() {
        val now = System.currentTimeMillis()

        viewModelScope.launch {
            combine(
                userRepository.getUserProgress(),
                cardRepository.getDueCardCount(now),
            ) { progress, dueCount ->
                val rankProgress = rankSystem.getProgress(progress.xp)

                // Check whether the streak is still valid for today so the
                // displayed count is accurate even if the user hasn't practiced yet.
                val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                val streakResult = streakManager.checkStreak(
                    currentCount = progress.streakCount,
                    lastDateStr  = progress.streakLastDate,
                )

                val mission = buildDailyMission(dueCount, progress.onboardingComplete)

                HomeUiState(
                    rankTitle          = rankProgress.currentRank.title,
                    xpCurrent          = progress.xp,
                    xpForNext          = rankProgress.xpForNext,
                    rankProgress       = rankProgress.progress,
                    streakCount        = streakResult.newCount,
                    dueReviewCount     = dueCount,
                    newCardCount       = 0, // loaded below to avoid blocking here
                    accuracy           = 0f, // loaded below from today's stats
                    dailyMission       = mission,
                    onboardingComplete = progress.onboardingComplete,
                    isLoading          = false,
                )
            }.collect { state ->
                _uiState.value = state
                // After the base state lands, refresh the stats overlay.
                loadTodayStats()
            }
        }
    }

    /**
     * Load today's accuracy and new-card count, then patch them into the
     * existing state without triggering a full recompose of the rank/streak
     * section.
     */
    private fun loadTodayStats() {
        viewModelScope.launch {
            val stats        = userRepository.getTodayStats()
            val newCardCount = cardRepository.getNewCards(limit = 10).size

            _uiState.update { current ->
                current.copy(
                    accuracy     = stats.accuracy,
                    newCardCount = newCardCount,
                )
            }
        }
    }

    // ── Mission text ─────────────────────────────────────────────────────────

    private fun buildDailyMission(dueCount: Int, onboardingComplete: Boolean): String = when {
        !onboardingComplete -> "Complete your first quiz session"
        dueCount == 0       -> "All caught up — try a new session!"
        dueCount == 1       -> "Review 1 card to stay on track"
        dueCount < 10       -> "Review $dueCount cards due today"
        else                -> "Review your $dueCount due cards"
    }
}
