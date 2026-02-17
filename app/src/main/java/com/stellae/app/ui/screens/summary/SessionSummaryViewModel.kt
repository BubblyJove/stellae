package com.stellae.app.ui.screens.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.stellae.app.domain.gamification.RankSystem
import com.stellae.app.domain.model.Rank
import com.stellae.app.domain.model.SessionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel for the Session Summary screen.
 *
 * [SessionResult] can be supplied in one of two ways:
 *
 * 1. Directly via [provideResult] — called by the nav graph immediately after
 *    the Quiz screen navigates here (the result is held in the QuizViewModel
 *    and passed as a navigation callback argument).
 *
 * 2. Via [SavedStateHandle] — if the app process is killed and restored,
 *    Android will rehydrate the handle.  In that case a JSON-encoded result
 *    string can be stored under [KEY_SESSION_RESULT]; alternatively the
 *    screen falls back to a sensible empty state.
 *
 * The ViewModel exposes a single [uiState] flow for the UI to observe.
 */
@HiltViewModel
class SessionSummaryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val rankSystem: RankSystem,
) : ViewModel() {

    companion object {
        /** SavedStateHandle key for a serialised SessionResult (optional). */
        const val KEY_SESSION_RESULT = "session_result"
    }

    data class SummaryUiState(
        val cardsReviewed: Int      = 0,
        val correctCount: Int       = 0,
        val accuracy: Float         = 0f,
        val xpEarned: Long          = 0L,
        val durationSeconds: Int    = 0,
        val streakCount: Int        = 0,
        val rankTitle: String       = "Neophyte",
        val xpCurrent: Long         = 0L,
        val xpForNext: Long         = 500L,
        val rankProgress: Float     = 0f,
        val didRankUp: Boolean      = false,
        val newRankTitle: String    = "",
        val tip: String             = "",
        val weakAreas: List<String> = emptyList(),
    )

    private val _uiState = MutableStateFlow(SummaryUiState())
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    /**
     * Populate the summary from a [SessionResult] that was produced by the
     * Quiz screen and handed directly to this ViewModel via the navigation
     * callback.
     */
    fun provideResult(result: SessionResult) {
        val rankProgress = rankSystem.getProgress(result.xpAfter)
        val didRankUp    = result.rankAfter.level > result.rankBefore.level

        val accuracy = if (result.cardsReviewed > 0) {
            result.correctCount.toFloat() / result.cardsReviewed.toFloat()
        } else 0f

        _uiState.value = SummaryUiState(
            cardsReviewed   = result.cardsReviewed,
            correctCount    = result.correctCount,
            accuracy        = accuracy,
            xpEarned        = result.xpEarned,
            durationSeconds = result.durationSeconds,
            streakCount     = result.newStreakCount,
            rankTitle       = rankProgress.currentRank.title,
            xpCurrent       = result.xpAfter,
            xpForNext       = rankProgress.xpForNext,
            rankProgress    = rankProgress.progress,
            didRankUp       = didRankUp,
            newRankTitle    = if (didRankUp) result.rankAfter.title else "",
            tip             = result.tip,
            weakAreas       = result.weakAreas,
        )
    }
}
