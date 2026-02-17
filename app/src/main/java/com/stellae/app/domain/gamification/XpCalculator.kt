package com.stellae.app.domain.gamification

import javax.inject.Inject

/**
 * Calculates XP awarded for a single answered card.
 *
 * Formula:
 *   base = BASE_XP + (difficultyTier * DIFFICULTY_BONUS_MULTIPLIER)
 *   if responseTime < SPEED_THRESHOLD_MS: base += SPEED_BONUS
 *   multiplier = clamp(STREAK_MULTIPLIER_BASE + streakCount * STREAK_MULTIPLIER_INCREMENT,
 *                      STREAK_MULTIPLIER_BASE, MAX_STREAK_MULTIPLIER)
 *   xpEarned = (base * multiplier).toLong()
 *
 * Always returns 0 for incorrect answers.
 */
class XpCalculator @Inject constructor() {

    companion object {
        const val BASE_XP: Long = 10L
        const val DIFFICULTY_BONUS_MULTIPLIER: Long = 2L
        const val SPEED_THRESHOLD_MS: Long = 3_000L
        const val SPEED_BONUS: Long = 5L
        const val STREAK_MULTIPLIER_BASE: Float = 1.0f
        const val STREAK_MULTIPLIER_INCREMENT: Float = 0.02f  // +2% per streak day
        const val MAX_STREAK_MULTIPLIER: Float = 2.0f
    }

    /**
     * @param isCorrect       Whether the user answered correctly.
     * @param difficultyTier  Card difficulty tier (1-5); higher tiers give more XP.
     * @param responseTimeMs  Time taken to answer in milliseconds.
     * @param streakCount     Current daily streak; increases the XP multiplier.
     * @return XP earned (0 if incorrect).
     */
    fun calculate(
        isCorrect: Boolean,
        difficultyTier: Int,
        responseTimeMs: Long,
        streakCount: Int
    ): Long {
        if (!isCorrect) return 0L

        var xp = BASE_XP
        xp += difficultyTier.coerceAtLeast(1) * DIFFICULTY_BONUS_MULTIPLIER

        if (responseTimeMs in 1L until SPEED_THRESHOLD_MS) {
            xp += SPEED_BONUS
        }

        val streakMultiplier = (STREAK_MULTIPLIER_BASE + streakCount.coerceAtLeast(0) * STREAK_MULTIPLIER_INCREMENT)
            .coerceIn(STREAK_MULTIPLIER_BASE, MAX_STREAK_MULTIPLIER)

        return (xp * streakMultiplier).toLong()
    }
}
