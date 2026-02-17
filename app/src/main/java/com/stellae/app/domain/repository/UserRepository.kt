package com.stellae.app.domain.repository

import com.stellae.app.domain.model.Rank
import com.stellae.app.domain.model.SessionResult
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    /** Continuously emit the latest [UserProgress] snapshot. */
    fun getUserProgress(): Flow<UserProgress>

    /** Increment the user's XP by [amount] and persist the result. */
    suspend fun addXp(amount: Long)

    /**
     * Update the current streak.
     *
     * @param count The new streak count (post-evaluation).
     * @param date  The date string representing when the streak was last updated (ISO-8601, e.g. "2025-06-01").
     */
    suspend fun updateStreak(count: Int, date: String)

    /** Persist a completed session to the session log. */
    suspend fun logSession(session: SessionResult)

    /** Emit the [limit] most recent session log entries, newest first. */
    fun getRecentSessions(limit: Int): Flow<List<SessionLogEntry>>

    /** Return aggregated stats for the current calendar day. */
    suspend fun getTodayStats(): TodayStats
}

// ------------------------------------------------------------------
// Supporting data classes
// ------------------------------------------------------------------

data class UserProgress(
    val xp: Long,
    val rank: Rank,
    val streakCount: Int,
    val streakLastDate: String,
    val freezePotions: Int,
    val weeklyGoalDays: Int,
    val totalCardsReviewed: Long,
    val totalCorrect: Long,
    val onboardingComplete: Boolean
)

data class SessionLogEntry(
    val timestamp: Long,
    val cardsReviewed: Int,
    val correctCount: Int,
    val xpEarned: Long,
    val durationSeconds: Int
)

data class TodayStats(
    val cardsReviewed: Int,
    val correctCount: Int,
    val accuracy: Float
)
