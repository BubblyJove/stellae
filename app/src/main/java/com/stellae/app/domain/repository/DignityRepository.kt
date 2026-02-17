package com.stellae.app.domain.repository

import com.stellae.app.domain.model.Element
import com.stellae.app.domain.model.Planet
import com.stellae.app.domain.model.Sign

interface DignityRepository {

    // ------------------------------------------------------------------
    // Domicile
    // ------------------------------------------------------------------

    /** Return the planet that rules [signId] by domicile, or `null`. */
    suspend fun getDomicileRuler(signId: Int): Planet?

    /** Return all signs in which [planetId] has domicile rulership. */
    suspend fun getDomicileSigns(planetId: Int): List<Sign>

    // ------------------------------------------------------------------
    // Exaltation
    // ------------------------------------------------------------------

    /**
     * Return the sign and exact degree of [planetId]'s exaltation, or
     * `null` if the planet has no traditional exaltation.
     */
    suspend fun getExaltation(planetId: Int): Pair<Sign, Int>?

    /** Return the planet exalted in [signId], or `null`. */
    suspend fun getExaltedPlanet(signId: Int): Planet?

    // ------------------------------------------------------------------
    // Triplicity
    // ------------------------------------------------------------------

    /**
     * Return the primary triplicity ruler for an [element] and chart sect.
     * [isDayChart] selects the diurnal ruler when `true`, nocturnal when `false`.
     */
    suspend fun getTriplicityRulers(element: Element, isDayChart: Boolean): Planet?

    // ------------------------------------------------------------------
    // Terms (bounds)
    // ------------------------------------------------------------------

    /** Return the planet holding the Egyptian term for [degree] in [signId]. */
    suspend fun getTermRuler(signId: Int, degree: Int): Planet?

    /**
     * Return an ordered list of (planet, degree range) pairs describing all
     * term divisions within [signId].
     */
    suspend fun getTermsForSign(signId: Int): List<Pair<Planet, IntRange>>

    // ------------------------------------------------------------------
    // Decans (faces)
    // ------------------------------------------------------------------

    /** Return the decan ruler for [degree] (0-29) in [signId]. */
    suspend fun getDecanRuler(signId: Int, degree: Int): Planet?

    // ------------------------------------------------------------------
    // Reference data
    // ------------------------------------------------------------------

    suspend fun getAllPlanets(): List<Planet>
    suspend fun getAllSigns(): List<Sign>
    suspend fun getPlanetById(id: Int): Planet?
    suspend fun getSignById(id: Int): Sign?
}
