package com.stellae.app.domain.fsrs

import com.stellae.app.domain.model.CardState
import com.stellae.app.domain.model.FsrsState
import com.stellae.app.domain.model.Rating
import kotlin.math.exp
import kotlin.math.pow

/**
 * FSRS-5 Spaced Repetition Scheduler.
 *
 * Core formulas:
 *   Retrievability:  R(t) = (1 + t / (9 * S))^(-1)
 *   Stability (success): S' = S * (1 + e^w8 * (11 - D) * S^(-w9) * (e^(w10*(1-R)) - 1))
 *   Stability (failure): S' = w11 * D^(-w12) * ((S+1)^w13 - 1) * e^(w14*(1-R))
 *   Difficulty update:   D' = clamp(D - w5*(rating - 3), 1, 10)
 *   Interval (90% retention): I = 9 * S   (in days)
 */
class FsrsScheduler(
    private val desiredRetention: Float = 0.9f
) {

    // FSRS-5 default weight vector (indices 0–16)
    // w0..w3  : initial stability per rating (Again, Hard, Good, Easy) in days
    // w4      : initial difficulty mean (D_init)
    // w5      : difficulty delta per rating step
    // w6      : mean-reversion weight for difficulty
    // w7      : reserved / decay
    // w8      : stability-growth exponent constant
    // w9      : stability power factor (success)
    // w10     : retrievability factor in stability growth
    // w11     : failure stability base coefficient
    // w12     : failure difficulty exponent
    // w13     : failure stability power factor
    // w14     : failure retrievability exponent
    // w15,w16 : reserved
    private val w = floatArrayOf(
        0.4f,   // w0  – S0 for Again
        0.9f,   // w1  – S0 for Hard
        2.5f,   // w2  – S0 for Good
        6.0f,   // w3  – S0 for Easy
        7.0f,   // w4  – D_init
        0.5f,   // w5  – difficulty update rate
        0.8f,   // w6  – difficulty mean-reversion weight
        0.3f,   // w7  – difficulty reversion rate
        1.2f,   // w8  – stability success exponent
        0.2f,   // w9  – stability success power
        1.0f,   // w10 – retrievability factor in success formula
        0.5f,   // w11 – failure stability coefficient
        0.2f,   // w12 – failure difficulty exponent
        0.1f,   // w13 – failure stability power
        0.0f,   // w14 – failure retrievability exponent
        0.0f,   // w15 – reserved
        0.0f    // w16 – reserved
    )

    private val millisPerDay: Long = 86_400_000L

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /**
     * Process a review and return the updated [FsrsState].
     *
     * @param state   Current FSRS state for this card.
     * @param rating  User's self-rating (AGAIN / HARD / GOOD / EASY).
     * @param now     Current epoch-millis timestamp.
     */
    fun review(state: FsrsState, rating: Rating, now: Long): FsrsState {
        return if (state.state == CardState.NEW) {
            // First review – initialise difficulty and stability from rating
            val difficulty = getInitialDifficulty(rating)
            val stability = getInitialStability(rating)
            val interval = getInterval(stability)
            val nextReview = now + interval
            val newCardState = if (rating == Rating.AGAIN) CardState.LEARNING else CardState.REVIEW

            state.copy(
                difficulty = difficulty,
                stability = stability,
                retrievability = 1.0f,
                reps = 1,
                lapses = 0,
                lastReview = now,
                nextReview = nextReview,
                state = newCardState
            )
        } else {
            // Subsequent review
            val elapsedDays = elapsedDays(state.lastReview, now)
            val retrievability = getRetrievability(state, now)
            val newDifficulty = updateDifficulty(state, rating)
            val newStability = updateStability(
                state.copy(difficulty = newDifficulty),
                rating,
                retrievability
            )
            val interval = getInterval(newStability)
            val nextReview = now + interval

            val newLapses = if (rating == Rating.AGAIN) state.lapses + 1 else state.lapses
            val newCardState = when {
                rating == Rating.AGAIN -> CardState.RELEARNING
                state.state == CardState.RELEARNING && rating != Rating.AGAIN -> CardState.REVIEW
                else -> CardState.REVIEW
            }

            state.copy(
                difficulty = newDifficulty,
                stability = newStability,
                retrievability = retrievability,
                reps = state.reps + 1,
                lapses = newLapses,
                lastReview = now,
                nextReview = nextReview,
                state = newCardState
            )
        }
    }

    /**
     * Compute the current retrievability for a card given [now].
     * R(t) = (1 + t / (9 * S))^(-1)
     */
    fun getRetrievability(state: FsrsState, now: Long): Float {
        if (state.state == CardState.NEW || state.stability <= 0f) return 1.0f
        val elapsedDays = elapsedDays(state.lastReview, now).coerceAtLeast(0.0)
        val result = (1.0 + elapsedDays / (9.0 * state.stability)).pow(-1.0)
        return result.toFloat().coerceIn(0f, 1f)
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    /**
     * Initial stability (in days) based on the first rating.
     * Maps directly to w0..w3.
     */
    private fun getInitialStability(rating: Rating): Float {
        return when (rating) {
            Rating.AGAIN -> w[0]
            Rating.HARD  -> w[1]
            Rating.GOOD  -> w[2]
            Rating.EASY  -> w[3]
        }
    }

    /**
     * Initial difficulty for a brand-new card.
     * D0 = clamp(D_init - (rating - 3) * w5, 1, 10)
     * Using w4 as D_init (≈7) and w5 as the per-step delta.
     */
    private fun getInitialDifficulty(rating: Rating): Float {
        val dInit = w[4]          // 7.0
        val delta = w[5]          // 0.5
        val raw = dInit - (rating.value - 3) * delta
        return raw.coerceIn(1f, 10f)
    }

    /**
     * Difficulty after a subsequent review.
     * D' = clamp(D - w5*(rating - 3), 1, 10)
     *
     * A small mean-reversion term keeps extreme difficulties from drifting
     * permanently: D' += w6 * (D_init - D') before clamping.
     */
    private fun updateDifficulty(state: FsrsState, rating: Rating): Float {
        val delta = w[5] * (rating.value - 3)
        val adjusted = state.difficulty - delta
        // Mean reversion toward w4 (D_init)
        val reverted = adjusted + w[6] * (w[4] - adjusted) * w[7]
        return reverted.coerceIn(1f, 10f)
    }

    /**
     * New stability after a review.
     *
     * Success (HARD / GOOD / EASY):
     *   S' = S * (1 + e^w8 * (11 - D) * S^(-w9) * (e^(w10*(1-R)) - 1))
     *
     * Failure (AGAIN):
     *   S' = w11 * D^(-w12) * ((S+1)^w13 - 1) * e^(w14*(1-R))
     */
    private fun updateStability(state: FsrsState, rating: Rating, retrievability: Float): Float {
        val s = state.stability.toDouble()
        val d = state.difficulty.toDouble()
        val r = retrievability.toDouble().coerceIn(0.001, 1.0)

        return if (rating == Rating.AGAIN) {
            // Forgetting stability
            val part1 = w[11].toDouble()
            val part2 = d.pow(-w[12].toDouble())
            val part3 = (s + 1.0).pow(w[13].toDouble()) - 1.0
            val part4 = exp(w[14].toDouble() * (1.0 - r))
            val result = part1 * part2 * part3 * part4
            result.toFloat().coerceAtLeast(0.1f)
        } else {
            // Recall stability — Hard gets a dampening multiplier
            val hardPenalty = if (rating == Rating.HARD) 0.8 else 1.0
            val easyBonus  = if (rating == Rating.EASY) 1.3 else 1.0

            val growth = exp(w[8].toDouble()) *
                (11.0 - d) *
                s.pow(-w[9].toDouble()) *
                (exp(w[10].toDouble() * (1.0 - r)) - 1.0)

            val result = s * (1.0 + growth * hardPenalty * easyBonus)
            result.toFloat().coerceAtLeast(s.toFloat())
        }
    }

    /**
     * Convert a stability value (days) to a review interval in milliseconds.
     * At desired retention r_d, the interval is:
     *   I = S * (r_d^(1/(1 - 1/r_d)) - 1) ≈ S * 9  for r_d = 0.9
     *
     * We compute this exactly for the configured [desiredRetention].
     */
    private fun getInterval(stability: Float): Long {
        // From R(I) = desiredRetention = (1 + I/(9*S))^(-1)
        // => I = 9 * S * (desiredRetention^(-1) - 1)
        val intervalDays = 9.0 * stability * ((1.0 / desiredRetention) - 1.0)
        val days = intervalDays.coerceAtLeast(1.0)
        return (days * millisPerDay).toLong()
    }

    /** Elapsed time in fractional days between two epoch-millis timestamps. */
    private fun elapsedDays(lastReview: Long, now: Long): Double {
        return ((now - lastReview).toDouble() / millisPerDay).coerceAtLeast(0.0)
    }
}
