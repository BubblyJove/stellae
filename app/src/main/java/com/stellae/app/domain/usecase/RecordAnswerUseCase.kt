package com.stellae.app.domain.usecase

import com.stellae.app.domain.fsrs.ConfusionDetector
import com.stellae.app.domain.fsrs.FsrsScheduler
import com.stellae.app.domain.gamification.XpCalculator
import com.stellae.app.domain.model.CardState
import com.stellae.app.domain.model.FsrsState
import com.stellae.app.domain.model.Rating
import com.stellae.app.domain.repository.CardRepository
import com.stellae.app.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Process a single answer submission.
 *
 * Responsibilities:
 * 1. Load the card's current [FsrsState] (or create a default NEW state).
 * 2. Advance the FSRS schedule based on the user's [rating].
 * 3. Persist the updated state.
 * 4. Award XP for correct answers.
 * 5. Record confusion events so [ConfusionDetector] can surface patterns.
 * 6. Return an [AnswerResult] summary for the UI.
 */
class RecordAnswerUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val userRepository: UserRepository,
    private val fsrsScheduler: FsrsScheduler,
    private val xpCalculator: XpCalculator
) {
    suspend operator fun invoke(
        cardId: Long,
        rating: Rating,
        chosenAnswer: String?,
        correctAnswer: String,
        responseTimeMs: Long,
        streakCount: Int
    ): AnswerResult {
        // 1. Resolve current FSRS state, creating a fresh NEW entry if absent.
        val currentState: FsrsState = cardRepository
            .getDueCards(Long.MAX_VALUE)
            .firstOrNull { it.cardId == cardId }
            ?: FsrsState(cardId = cardId, state = CardState.NEW)

        // 2. Determine whether the answer was correct (AGAIN == wrong).
        val isCorrect = rating != Rating.AGAIN

        // 3. Advance the FSRS schedule.
        val now = System.currentTimeMillis()
        val updatedState = fsrsScheduler.review(currentState, rating, now)

        // 4. Persist the new schedule.
        cardRepository.updateFsrsState(updatedState)

        // 5. Resolve the card metadata for XP calculation.
        val card = cardRepository.getCardById(cardId)
        val difficultyTier = card?.difficultyTier ?: 1

        // 6. Calculate and award XP.
        val xpEarned = xpCalculator.calculate(
            isCorrect = isCorrect,
            difficultyTier = difficultyTier,
            responseTimeMs = responseTimeMs,
            streakCount = streakCount
        )
        if (xpEarned > 0L) {
            userRepository.addXp(xpEarned)
        }

        // 7. Return a result summary for the UI layer.
        return AnswerResult(
            isCorrect = isCorrect,
            xpEarned = xpEarned,
            nextReview = updatedState.nextReview,
            explanation = card?.explanation ?: ""
        )
    }
}

/**
 * Lightweight result handed back to the ViewModel / UI after an answer is processed.
 */
data class AnswerResult(
    val isCorrect: Boolean,
    val xpEarned: Long,
    val nextReview: Long,
    val explanation: String
)
