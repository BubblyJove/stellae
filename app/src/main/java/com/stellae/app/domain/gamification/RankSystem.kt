package com.stellae.app.domain.gamification

import com.stellae.app.domain.model.Rank
import javax.inject.Inject

/**
 * Provides rank-related queries and progress calculations.
 */
class RankSystem @Inject constructor() {

    /** Return the [Rank] that corresponds to the given [xp] total. */
    fun getCurrentRank(xp: Long): Rank = Rank.fromXp(xp)

    /**
     * Return a [RankProgress] snapshot showing how far the user has advanced
     * within their current rank toward the next one.
     *
     * When the user is at the maximum rank [progress] is clamped to 1.0f and
     * [RankProgress.nextRank] is `null`.
     */
    fun getProgress(xp: Long): RankProgress {
        val current = getCurrentRank(xp)
        val next = Rank.entries.getOrNull(current.ordinal + 1)

        val xpIntoRank = xp - current.xpRequired
        val xpForNextRank = (next?.xpRequired ?: current.xpRequired) - current.xpRequired

        val progress = if (xpForNextRank > 0L) {
            xpIntoRank.toFloat() / xpForNextRank.toFloat()
        } else {
            1f
        }

        return RankProgress(
            currentRank = current,
            nextRank = next,
            xpCurrent = xp,
            xpForNext = next?.xpRequired ?: xp,
            progress = progress.coerceIn(0f, 1f)
        )
    }

    /**
     * Detect a rank-up event.
     *
     * @return The new [Rank] if the XP transition from [xpBefore] to [xpAfter]
     *         crossed a rank boundary, otherwise `null`.
     */
    fun checkLevelUp(xpBefore: Long, xpAfter: Long): Rank? {
        val rankBefore = getCurrentRank(xpBefore)
        val rankAfter = getCurrentRank(xpAfter)
        return if (rankAfter.level > rankBefore.level) rankAfter else null
    }
}

/**
 * A snapshot of the user's position within their current rank tier.
 *
 * @param currentRank The rank the user currently holds.
 * @param nextRank    The rank directly above, or `null` at maximum rank.
 * @param xpCurrent   Total XP the user has accumulated.
 * @param xpForNext   Total XP threshold required for [nextRank] (equals
 *                    [xpCurrent] when already at max rank).
 * @param progress    Value in [0.0, 1.0] representing how close the user is
 *                    to reaching [nextRank]. 1.0 at maximum rank.
 */
data class RankProgress(
    val currentRank: Rank,
    val nextRank: Rank?,
    val xpCurrent: Long,
    val xpForNext: Long,
    val progress: Float
)
