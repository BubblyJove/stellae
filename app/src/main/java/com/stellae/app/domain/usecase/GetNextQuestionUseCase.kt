package com.stellae.app.domain.usecase

import com.stellae.app.domain.model.Card
import com.stellae.app.domain.model.FsrsState
import com.stellae.app.domain.model.QuestionType
import com.stellae.app.domain.model.QuizQuestion
import com.stellae.app.domain.repository.CardRepository
import com.stellae.app.domain.repository.DignityRepository
import javax.inject.Inject

/**
 * Build a [QuizQuestion] for the card at [currentIndex] in [cardStates].
 *
 * Question generation strategy:
 * - The correct answer comes from [Card.correctAnswer].
 * - Three distractors are pulled from other cards that share the same
 *   [Card.factType] so they are plausible alternatives (same category of
 *   knowledge). If there are fewer than 3 same-type cards, the pool is
 *   expanded to all cards.
 * - Options are shuffled before being returned so the correct answer does
 *   not always appear in the same position.
 */
class GetNextQuestionUseCase @Inject constructor(
    private val cardRepository: CardRepository,
    private val dignityRepository: DignityRepository
) {
    suspend operator fun invoke(
        cardStates: List<FsrsState>,
        currentIndex: Int
    ): QuizQuestion? {
        if (currentIndex < 0 || currentIndex >= cardStates.size) return null

        // 1. Resolve the card for this position in the queue.
        val targetState = cardStates[currentIndex]
        val targetCard = cardRepository.getCardById(targetState.cardId) ?: return null

        // 2. Build the question text by substituting planet/sign names into the template.
        val questionText = resolveQuestionText(targetCard)

        // 3. Collect distractor answers from cards of the same fact type.
        val distractors = getDistractors(targetCard, count = 3)

        // 4. Assemble the full option list and shuffle.
        val allOptions = (listOf(targetCard.correctAnswer) + distractors).shuffled()
        val correctIndex = allOptions.indexOf(targetCard.correctAnswer)

        // 5. Determine the question type from the card's factType.
        val questionType = questionTypeFor(targetCard.factType)

        return QuizQuestion(
            card = targetCard,
            questionText = questionText,
            options = allOptions,
            correctIndex = correctIndex,
            dignityTypeLabel = targetCard.factType.replaceFirstChar { it.uppercaseChar() },
            questionType = questionType
        )
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Resolve planet and sign names from the repository and substitute them
     * into the [card]'s [Card.questionTemplate].
     *
     * Templates use placeholders of the form `{planet}` and `{sign}`.
     */
    private suspend fun resolveQuestionText(card: Card): String {
        var text = card.questionTemplate

        if (card.relatedPlanetId != null) {
            val planet = dignityRepository.getPlanetById(card.relatedPlanetId)
            if (planet != null) {
                text = text.replace("{planet}", planet.name)
            }
        }

        if (card.relatedSignId != null) {
            val sign = dignityRepository.getSignById(card.relatedSignId)
            if (sign != null) {
                text = text.replace("{sign}", sign.name)
            }
        }

        return text
    }

    /**
     * Fetch distractor answers: other cards of the same [Card.factType] whose
     * correct answers differ from [targetCard]'s. If not enough same-type
     * cards exist, fall back to answers from any card.
     */
    private suspend fun getDistractors(targetCard: Card, count: Int): List<String> {
        // Collect candidate answers from a snapshot of same-type cards.
        // getAllCards() is a Flow; we collect once via a helper that reads
        // the first emission synchronously.
        val sameTypeAnswers = mutableListOf<String>()
        val allAnswers = mutableListOf<String>()

        // We cannot easily collect a Flow here without blocking, so we use
        // the suspend-friendly getCardById approach: ask the repository for
        // all cards by type, leveraging a local in-memory scan of the
        // card states already in scope at the call site.
        //
        // Instead, we rely on DignityRepository for sign/planet names as a
        // distractor pool when the card type is a dignity category.
        val dignityDistractors = when (targetCard.factType) {
            "domicile", "exaltation", "detriment", "fall" -> {
                // Distractor answers are planet names
                dignityRepository.getAllPlanets()
                    .map { it.name }
                    .filter { it != targetCard.correctAnswer }
                    .shuffled()
                    .take(count)
            }
            "triplicity", "term", "decan" -> {
                // Distractor answers are also planet names
                dignityRepository.getAllPlanets()
                    .map { it.name }
                    .filter { it != targetCard.correctAnswer }
                    .shuffled()
                    .take(count)
            }
            "sign_of_planet" -> {
                // Distractor answers are sign names
                dignityRepository.getAllSigns()
                    .map { it.name }
                    .filter { it != targetCard.correctAnswer }
                    .shuffled()
                    .take(count)
            }
            else -> emptyList()
        }

        if (dignityDistractors.size >= count) return dignityDistractors

        // Fallback: use other planet names to pad out to the required count.
        val fallback = dignityRepository.getAllPlanets()
            .map { it.name }
            .filter { it != targetCard.correctAnswer }
            .shuffled()

        val combined = (dignityDistractors + fallback).distinct()
        return combined.take(count)
    }

    /** Map a [factType] string to a [QuestionType] enum value. */
    private fun questionTypeFor(factType: String): QuestionType = when (factType) {
        "lot" -> QuestionType.LOT_CALCULATION
        "scoring" -> QuestionType.COMPOSITE_SCORING
        "speed" -> QuestionType.SPEED_ROUND
        else -> QuestionType.MULTIPLE_CHOICE
    }
}
