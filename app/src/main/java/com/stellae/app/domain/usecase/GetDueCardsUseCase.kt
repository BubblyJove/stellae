package com.stellae.app.domain.usecase

import com.stellae.app.domain.model.FsrsState
import com.stellae.app.domain.repository.CardRepository
import javax.inject.Inject

/**
 * Assemble the list of cards to show in a single study session.
 *
 * Strategy:
 * 1. Fetch all cards whose next-review timestamp is in the past (due cards).
 * 2. If fewer than 10 due cards exist, top up with brand-new cards so that
 *    the session always has enough material.
 * 3. Cap new-card intake at [maxNewCards] to avoid overwhelming the learner.
 */
class GetDueCardsUseCase @Inject constructor(
    private val cardRepository: CardRepository
) {
    /**
     * @param maxNewCards Maximum new (never-reviewed) cards to introduce.
     *                    Defaults to 5; clamped to leave room within the
     *                    10-card session cap.
     * @return Ordered list: due cards first, then new cards.
     */
    suspend operator fun invoke(maxNewCards: Int = 5): List<FsrsState> {
        val now = System.currentTimeMillis()

        val dueCards = cardRepository.getDueCards(now)

        val newCards = if (dueCards.size < 10) {
            // How many slots remain in the session (up to the caller's cap)
            val slotsAvailable = (10 - dueCards.size).coerceAtMost(maxNewCards)
            cardRepository.getNewCards(slotsAvailable)
        } else {
            emptyList()
        }

        return dueCards + newCards
    }
}
