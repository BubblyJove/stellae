package com.stellae.app.data.repository

import com.stellae.app.data.local.dao.SessionLogDao
import com.stellae.app.data.local.dao.UserProgressDao
import com.stellae.app.data.local.entity.SessionLogEntity
import com.stellae.app.data.local.entity.UserProgressEntity
import com.stellae.app.domain.model.Rank
import com.stellae.app.domain.model.SessionResult
import com.stellae.app.domain.repository.SessionLogEntry
import com.stellae.app.domain.repository.TodayStats
import com.stellae.app.domain.repository.UserProgress
import com.stellae.app.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProgressRepositoryImpl @Inject constructor(
    private val userProgressDao: UserProgressDao,
    private val sessionLogDao: SessionLogDao,
) : UserRepository {

    // ── Mapping helpers ──────────────────────────────────────────────────────

    /**
     * Map [UserProgressEntity] to [UserProgress].
     * [UserProgressEntity.rankLevel] is stored but we derive the current [Rank]
     * from the accumulated XP so that rank is always consistent with XP.
     */
    private fun UserProgressEntity.toDomain(): UserProgress = UserProgress(
        xp                 = xp,
        rank               = Rank.fromXp(xp),
        streakCount        = streakCount,
        streakLastDate     = streakLastDate,
        freezePotions      = freezePotions,
        weeklyGoalDays     = weeklyGoalDays,
        totalCardsReviewed = totalCardsReviewed,
        totalCorrect       = totalCorrect,
        onboardingComplete = onboardingComplete,
    )

    private fun SessionLogEntity.toDomain(): SessionLogEntry = SessionLogEntry(
        timestamp      = timestamp,
        cardsReviewed  = cardsReviewed,
        correctCount   = correctCount,
        xpEarned       = xpEarned,
        durationSeconds = durationSeconds,
    )

    // ── UserRepository implementation ────────────────────────────────────────

    override fun getUserProgress(): Flow<UserProgress> =
        userProgressDao.get().map { entity ->
            entity?.toDomain() ?: defaultUserProgress()
        }

    override suspend fun addXp(amount: Long) {
        ensureProgressRowExists()
        userProgressDao.addXp(amount)
    }

    override suspend fun updateStreak(count: Int, date: String) {
        ensureProgressRowExists()
        userProgressDao.updateStreak(count, date)
    }

    override suspend fun logSession(session: SessionResult) {
        // Persist the session log entry.
        sessionLogDao.insert(
            SessionLogEntity(
                timestamp       = System.currentTimeMillis(),
                cardsReviewed   = session.cardsReviewed,
                correctCount    = session.correctCount,
                xpEarned        = session.xpEarned,
                durationSeconds = session.durationSeconds,
            )
        )

        // Also update total counters on the progress row.
        ensureProgressRowExists()
        val current = userProgressDao.getOnce() ?: return
        userProgressDao.upsert(
            current.copy(
                totalCardsReviewed = current.totalCardsReviewed + session.cardsReviewed,
                totalCorrect       = current.totalCorrect + session.correctCount,
            )
        )
    }

    override fun getRecentSessions(limit: Int): Flow<List<SessionLogEntry>> =
        sessionLogDao.getRecent(limit).map { list -> list.map { it.toDomain() } }

    override suspend fun getTodayStats(): TodayStats {
        val startOfToday = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val reviewed = sessionLogDao.getTotalReviewedSince(startOfToday) ?: 0
        val correct  = sessionLogDao.getTotalCorrectSince(startOfToday) ?: 0
        val accuracy = if (reviewed > 0) correct.toFloat() / reviewed.toFloat() else 0f

        return TodayStats(
            cardsReviewed = reviewed,
            correctCount  = correct,
            accuracy      = accuracy,
        )
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    /**
     * Guarantee that the singleton progress row (id = 1) exists before any
     * UPDATE query runs. Room's UPDATE silently does nothing on a missing row.
     */
    private suspend fun ensureProgressRowExists() {
        if (userProgressDao.getOnce() == null) {
            userProgressDao.upsert(UserProgressEntity())
        }
    }

    private fun defaultUserProgress(): UserProgress = UserProgress(
        xp                 = 0L,
        rank               = Rank.NEOPHYTE,
        streakCount        = 0,
        streakLastDate     = "",
        freezePotions      = 1,
        weeklyGoalDays     = 5,
        totalCardsReviewed = 0L,
        totalCorrect       = 0L,
        onboardingComplete = false,
    )
}
