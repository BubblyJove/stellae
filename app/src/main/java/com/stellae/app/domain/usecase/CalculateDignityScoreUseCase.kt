package com.stellae.app.domain.usecase

import com.stellae.app.domain.model.DignityScore
import com.stellae.app.domain.model.DignityType
import com.stellae.app.domain.model.Element
import com.stellae.app.domain.repository.DignityRepository
import javax.inject.Inject

/**
 * Calculate the full Ptolemaic essential dignity score for a planet placed at
 * a specific sign, degree, and chart sect.
 *
 * Scoring (per [DignityType]):
 *   Domicile   +5
 *   Exaltation +4
 *   Triplicity +3
 *   Term       +2
 *   Decan      +1
 *   Detriment  -5
 *   Fall       -4
 *   Peregrine   0  (no dignity or debility applies)
 *
 * Detriment and Fall are detected by checking the opposite sign relationships:
 *   - Detriment: the planet rules the sign directly opposite [signId].
 *   - Fall: the planet is exalted in the sign directly opposite [signId].
 *
 * Opposite sign arithmetic: opposite = ((signId - 1 + 6) % 12) + 1
 * (Signs are numbered 1-12, so Aries=1 is opposite Libra=7, etc.)
 */
class CalculateDignityScoreUseCase @Inject constructor(
    private val dignityRepository: DignityRepository
) {
    suspend operator fun invoke(
        planetId: Int,
        signId: Int,
        degree: Int,
        isDayChart: Boolean
    ): DignityScore {
        val accumulatedDignities = mutableListOf<DignityType>()

        // ------------------------------------------------------------------
        // Domicile — planet rules this sign
        // ------------------------------------------------------------------
        val domicileRuler = dignityRepository.getDomicileRuler(signId)
        val hasDomicile = domicileRuler?.id == planetId

        // ------------------------------------------------------------------
        // Exaltation — planet is exalted in this sign
        // ------------------------------------------------------------------
        val exaltedPlanet = dignityRepository.getExaltedPlanet(signId)
        val hasExaltation = exaltedPlanet?.id == planetId

        // ------------------------------------------------------------------
        // Triplicity — planet rules the element of this sign (day or night)
        // ------------------------------------------------------------------
        val sign = dignityRepository.getSignById(signId)
        val hasTriplicity = if (sign != null) {
            val triplicityRuler = dignityRepository.getTriplicityRulers(sign.element, isDayChart)
            triplicityRuler?.id == planetId
        } else false

        // ------------------------------------------------------------------
        // Term (Egyptian bounds)
        // ------------------------------------------------------------------
        val termRuler = dignityRepository.getTermRuler(signId, degree)
        val hasTerm = termRuler?.id == planetId

        // ------------------------------------------------------------------
        // Decan (face) — each sign has three 10-degree faces
        // ------------------------------------------------------------------
        val decanRuler = dignityRepository.getDecanRuler(signId, degree)
        val hasDecan = decanRuler?.id == planetId

        // ------------------------------------------------------------------
        // Detriment — planet rules the sign opposite this sign
        // ------------------------------------------------------------------
        val oppositeSignId = oppositeSign(signId)
        val oppositeRuler = dignityRepository.getDomicileRuler(oppositeSignId)
        val hasDetriment = oppositeRuler?.id == planetId

        // ------------------------------------------------------------------
        // Fall — planet is exalted in the sign opposite this sign
        // ------------------------------------------------------------------
        val oppositeExalted = dignityRepository.getExaltedPlanet(oppositeSignId)
        val hasFall = oppositeExalted?.id == planetId

        // ------------------------------------------------------------------
        // Accumulate dignities and compute total score
        // ------------------------------------------------------------------
        // Dignities and debilities can technically co-occur in edge-case
        // datasets but we include all that apply and let the score reflect them.
        if (hasDomicile)   accumulatedDignities.add(DignityType.DOMICILE)
        if (hasExaltation) accumulatedDignities.add(DignityType.EXALTATION)
        if (hasTriplicity) accumulatedDignities.add(DignityType.TRIPLICITY)
        if (hasTerm)       accumulatedDignities.add(DignityType.TERM)
        if (hasDecan)      accumulatedDignities.add(DignityType.DECAN)
        if (hasDetriment)  accumulatedDignities.add(DignityType.DETRIMENT)
        if (hasFall)       accumulatedDignities.add(DignityType.FALL)

        // Peregrine — no essential dignity or debility at all
        if (accumulatedDignities.isEmpty()) {
            accumulatedDignities.add(DignityType.PEREGRINE)
        }

        val totalScore = accumulatedDignities.sumOf { it.points }

        return DignityScore(
            planetId = planetId,
            signId = signId,
            degree = degree,
            isDayChart = isDayChart,
            domicile = hasDomicile,
            exaltation = hasExaltation,
            triplicity = hasTriplicity,
            term = hasTerm,
            decan = hasDecan,
            detriment = hasDetriment,
            fall = hasFall,
            totalScore = totalScore,
            dignities = accumulatedDignities.toList()
        )
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /**
     * Return the sign directly opposite [signId] on the zodiac wheel.
     * Signs 1-12: Aries(1) ↔ Libra(7), Taurus(2) ↔ Scorpio(8), etc.
     */
    private fun oppositeSign(signId: Int): Int = ((signId - 1 + 6) % 12) + 1
}
