package com.stellae.app.domain.gamification

/**
 * Catalogue of all achievement definitions in Stellae.
 *
 * An [AchievementDef] is a static description of an achievement — it does not
 * store progress. Progress tracking lives in the data layer (AchievementEntity).
 *
 * [starsTotal] is the number of sub-goals that must be completed to fully earn
 * the badge. Most milestone badges require only 1; constellation badges require
 * 5 (one per dignity type: domicile, exaltation, triplicity, term, decan).
 */
object AchievementSystem {

    data class AchievementDef(
        val id: String,
        val name: String,
        val description: String,
        val category: String,
        val starsTotal: Int
    )

    /** Complete list of achievement definitions. */
    val ACHIEVEMENTS: List<AchievementDef> = listOf(

        // ------------------------------------------------------------------
        // Constellation Badges (one per zodiac sign)
        // Earn all 5 dignity stars for the sign: domicile, exaltation,
        // triplicity, term, and decan rulers.
        // ------------------------------------------------------------------
        AchievementDef(
            id = "aries_constellation",
            name = "Ram's Horns",
            description = "Master all Aries dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "taurus_constellation",
            name = "Bull's Eye",
            description = "Master all Taurus dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "gemini_constellation",
            name = "Twin Stars",
            description = "Master all Gemini dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "cancer_constellation",
            name = "Crab Nebula",
            description = "Master all Cancer dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "leo_constellation",
            name = "Lion's Heart",
            description = "Master all Leo dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "virgo_constellation",
            name = "Maiden's Light",
            description = "Master all Virgo dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "libra_constellation",
            name = "Balanced Scales",
            description = "Master all Libra dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "scorpio_constellation",
            name = "Scorpion's Tail",
            description = "Master all Scorpio dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "sagittarius_constellation",
            name = "Archer's Arrow",
            description = "Master all Sagittarius dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "capricorn_constellation",
            name = "Sea-Goat's Horn",
            description = "Master all Capricorn dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "aquarius_constellation",
            name = "Water Bearer's Stream",
            description = "Master all Aquarius dignities",
            category = "constellation",
            starsTotal = 5
        ),
        AchievementDef(
            id = "pisces_constellation",
            name = "Fishes' Bond",
            description = "Master all Pisces dignities",
            category = "constellation",
            starsTotal = 5
        ),

        // ------------------------------------------------------------------
        // Milestone Badges
        // ------------------------------------------------------------------
        AchievementDef(
            id = "first_session",
            name = "First Light",
            description = "Complete your first session",
            category = "milestone",
            starsTotal = 1
        ),
        AchievementDef(
            id = "week_streak",
            name = "Consistent Scholar",
            description = "Maintain a 7-day streak",
            category = "milestone",
            starsTotal = 1
        ),
        AchievementDef(
            id = "month_streak",
            name = "Dedicated Student",
            description = "Maintain a 30-day streak",
            category = "milestone",
            starsTotal = 1
        ),
        AchievementDef(
            id = "speed_demon",
            name = "Mercury's Speed",
            description = "Answer 10 questions under 3 seconds each",
            category = "milestone",
            starsTotal = 10
        ),
        AchievementDef(
            id = "perfect_session",
            name = "Flawless",
            description = "Complete a session with 100% accuracy",
            category = "milestone",
            starsTotal = 1
        ),
        AchievementDef(
            id = "all_domiciles",
            name = "Keeper of Houses",
            description = "Master all domicile associations",
            category = "milestone",
            starsTotal = 1
        ),
        AchievementDef(
            id = "all_exaltations",
            name = "Throne Scholar",
            description = "Master all exaltation placements",
            category = "milestone",
            starsTotal = 1
        ),
        AchievementDef(
            id = "lot_master",
            name = "Fortune's Child",
            description = "Calculate all 7 lots correctly",
            category = "milestone",
            starsTotal = 7
        )
    )

    // ------------------------------------------------------------------
    // Convenience accessors
    // ------------------------------------------------------------------

    /** Return the [AchievementDef] with the given [id], or `null`. */
    fun findById(id: String): AchievementDef? = ACHIEVEMENTS.firstOrNull { it.id == id }

    /** Return all achievements in the given [category]. */
    fun byCategory(category: String): List<AchievementDef> =
        ACHIEVEMENTS.filter { it.category == category }

    /** All constellation achievements (one per zodiac sign). */
    val constellations: List<AchievementDef>
        get() = byCategory("constellation")

    /** All milestone achievements. */
    val milestones: List<AchievementDef>
        get() = byCategory("milestone")
}
