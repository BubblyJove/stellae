package com.stellae.app.domain.usecase

import javax.inject.Inject

/**
 * Calculate Arabic lot (Part / Lot of Fortune, Spirit, etc.) positions using
 * standard zodiacal arithmetic.
 *
 * All input and output positions are expressed as absolute degrees in the
 * range [0, 359], where 0 = 0° Aries, 30 = 0° Taurus, and so on.
 *
 * General formula: Lot = (Ascendant + A - B) mod 360
 *
 * Common lots:
 *   Lot of Fortune (day):  Asc + Moon - Sun
 *   Lot of Fortune (night): Asc + Sun - Moon
 *   Lot of Spirit (day):   Asc + Sun - Moon
 *   Lot of Spirit (night): Asc + Moon - Sun
 */
class CalculateLotUseCase @Inject constructor() {

    /**
     * Calculate a lot position.
     *
     * @param ascendantDegree Absolute degree of the Ascendant (0-359).
     * @param pointA          Absolute degree of the first planet/point.
     * @param pointB          Absolute degree of the second planet/point.
     * @return Absolute degree of the lot (0-359), always non-negative.
     */
    operator fun invoke(
        ascendantDegree: Int,
        pointA: Int,
        pointB: Int
    ): Int {
        return ((ascendantDegree + pointA - pointB) % 360 + 360) % 360
    }

    /**
     * Convert an absolute degree (0-359) to a (signId, degreeInSign) pair.
     *
     * Sign IDs run from 1 (Aries, 0°-29°) to 12 (Pisces, 330°-359°).
     *
     * @return Pair of (signId in 1..12, degree within that sign in 0..29).
     */
    fun getSignAndDegree(absoluteDegree: Int): Pair<Int, Int> {
        val clamped = ((absoluteDegree % 360) + 360) % 360
        val signId = (clamped / 30) + 1
        val degreeInSign = clamped % 30
        return signId to degreeInSign
    }

    /**
     * Calculate the Lot of Fortune.
     *
     * Day chart: Asc + Moon - Sun
     * Night chart: Asc + Sun - Moon
     *
     * @param ascendantDegree Absolute degree of the Ascendant.
     * @param sunDegree       Absolute degree of the Sun.
     * @param moonDegree      Absolute degree of the Moon.
     * @param isDayChart      `true` if the Sun is above the horizon.
     * @return Absolute degree of the Lot of Fortune (0-359).
     */
    fun lotOfFortune(
        ascendantDegree: Int,
        sunDegree: Int,
        moonDegree: Int,
        isDayChart: Boolean
    ): Int = if (isDayChart) {
        invoke(ascendantDegree, moonDegree, sunDegree)
    } else {
        invoke(ascendantDegree, sunDegree, moonDegree)
    }

    /**
     * Calculate the Lot of Spirit (Daemon).
     *
     * Day chart: Asc + Sun - Moon  (reversed from Fortune)
     * Night chart: Asc + Moon - Sun
     *
     * @param ascendantDegree Absolute degree of the Ascendant.
     * @param sunDegree       Absolute degree of the Sun.
     * @param moonDegree      Absolute degree of the Moon.
     * @param isDayChart      `true` if the Sun is above the horizon.
     * @return Absolute degree of the Lot of Spirit (0-359).
     */
    fun lotOfSpirit(
        ascendantDegree: Int,
        sunDegree: Int,
        moonDegree: Int,
        isDayChart: Boolean
    ): Int = if (isDayChart) {
        invoke(ascendantDegree, sunDegree, moonDegree)
    } else {
        invoke(ascendantDegree, moonDegree, sunDegree)
    }

    /**
     * Calculate the Lot of Eros.
     *
     * Day chart: Asc + Lot of Spirit - Venus
     * Night chart: Asc + Venus - Lot of Spirit
     *
     * @param ascendantDegree  Absolute degree of the Ascendant.
     * @param venusDegree      Absolute degree of Venus.
     * @param lotOfSpiritDeg   Absolute degree of the Lot of Spirit (pre-calculated).
     * @param isDayChart       `true` if the Sun is above the horizon.
     * @return Absolute degree of the Lot of Eros (0-359).
     */
    fun lotOfEros(
        ascendantDegree: Int,
        venusDegree: Int,
        lotOfSpiritDeg: Int,
        isDayChart: Boolean
    ): Int = if (isDayChart) {
        invoke(ascendantDegree, lotOfSpiritDeg, venusDegree)
    } else {
        invoke(ascendantDegree, venusDegree, lotOfSpiritDeg)
    }

    /**
     * Calculate the Lot of Necessity.
     *
     * Day chart: Asc + Lot of Fortune - Mercury
     * Night chart: Asc + Mercury - Lot of Fortune
     *
     * @param ascendantDegree  Absolute degree of the Ascendant.
     * @param mercuryDegree    Absolute degree of Mercury.
     * @param lotOfFortuneDeg  Absolute degree of the Lot of Fortune (pre-calculated).
     * @param isDayChart       `true` if the Sun is above the horizon.
     * @return Absolute degree of the Lot of Necessity (0-359).
     */
    fun lotOfNecessity(
        ascendantDegree: Int,
        mercuryDegree: Int,
        lotOfFortuneDeg: Int,
        isDayChart: Boolean
    ): Int = if (isDayChart) {
        invoke(ascendantDegree, lotOfFortuneDeg, mercuryDegree)
    } else {
        invoke(ascendantDegree, mercuryDegree, lotOfFortuneDeg)
    }
}
