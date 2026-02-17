package com.stellae.app.domain.gamification

import com.stellae.app.domain.model.Rank

/**
 * Represents a boss-challenge event that unlocks at a specific [Rank].
 *
 * Boss challenges are milestone quizzes that test mastery of a particular
 * dignity category (or a combination of categories) under stricter conditions
 * than regular review sessions — e.g. 100% accuracy requirements or a time
 * limit.
 *
 * @param id                Stable identifier used for persistence.
 * @param name              Display name of the antagonist / guardian figure.
 * @param title             Short label for the challenge (shown on unlock card).
 * @param description       One-sentence description of the challenge rules.
 * @param requiredRank      Minimum [Rank] the user must hold to attempt this challenge.
 * @param questionCount     Total number of questions in the challenge.
 * @param timeLimitSeconds  Optional overall time limit in seconds; `null` = unlimited.
 * @param requiredAccuracy  Minimum fraction of correct answers to pass (0.0–1.0).
 * @param dignityTypes      List of factType strings covered by this challenge.
 */
data class BossChallenge(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val requiredRank: Rank,
    val questionCount: Int,
    val timeLimitSeconds: Int?,
    val requiredAccuracy: Float,
    val dignityTypes: List<String>
) {
    companion object {

        /** Canonical list of all boss challenges, ordered by rank gate. */
        val ALL: List<BossChallenge> = listOf(
            BossChallenge(
                id = "domicile_boss",
                name = "The Guardian of Houses",
                title = "Domicile Boss",
                description = "Place all 7 planets in their domiciles in under 60 seconds",
                requiredRank = Rank.STUDENT,
                questionCount = 14,
                timeLimitSeconds = 60,
                requiredAccuracy = 1.0f,
                dignityTypes = listOf("domicile")
            ),
            BossChallenge(
                id = "exaltation_boss",
                name = "The Throne Keeper",
                title = "Exaltation Boss",
                description = "Identify all exaltation degrees from memory with no hints",
                requiredRank = Rank.ACOLYTE,
                questionCount = 7,
                timeLimitSeconds = null,
                requiredAccuracy = 1.0f,
                dignityTypes = listOf("exaltation")
            ),
            BossChallenge(
                id = "triplicity_boss",
                name = "The Elemental Sphinx",
                title = "Triplicity Boss",
                description = "Mixed day/night triplicity quiz with 90%+ accuracy",
                requiredRank = Rank.ADEPT,
                questionCount = 24,
                timeLimitSeconds = null,
                requiredAccuracy = 0.9f,
                dignityTypes = listOf("triplicity")
            ),
            BossChallenge(
                id = "terms_boss",
                name = "The Boundary Warden",
                title = "Terms Boss",
                description = "Identify term rulers for 12 consecutive signs",
                requiredRank = Rank.PRACTITIONER,
                questionCount = 12,
                timeLimitSeconds = null,
                requiredAccuracy = 1.0f,
                dignityTypes = listOf("term")
            ),
            BossChallenge(
                id = "decan_boss",
                name = "The Decanal Oracle",
                title = "Decan Boss",
                description = "Complete the full decan wheel from memory",
                requiredRank = Rank.SCHOLAR,
                questionCount = 36,
                timeLimitSeconds = null,
                requiredAccuracy = 0.9f,
                dignityTypes = listOf("decan")
            ),
            BossChallenge(
                id = "synthesis_boss",
                name = "The Chart Reader",
                title = "Synthesis Boss",
                description = "Score 5 planets' total essential dignity from a random chart",
                requiredRank = Rank.INTERPRETER,
                questionCount = 5,
                timeLimitSeconds = null,
                requiredAccuracy = 0.8f,
                dignityTypes = listOf("scoring")
            ),
            BossChallenge(
                id = "final_boss",
                name = "The Magister's Trial",
                title = "Final Boss",
                description = "All dignity types, lot calculations, and speed challenges",
                requiredRank = Rank.SAGE,
                questionCount = 50,
                timeLimitSeconds = 600,
                requiredAccuracy = 0.9f,
                dignityTypes = listOf(
                    "domicile", "exaltation", "triplicity",
                    "term", "decan", "lot", "scoring"
                )
            )
        )

        /** Look up a challenge by its stable [id], or return `null`. */
        fun findById(id: String): BossChallenge? = ALL.firstOrNull { it.id == id }

        /** Return all challenges that the user is eligible to attempt at [rank]. */
        fun availableFor(rank: Rank): List<BossChallenge> =
            ALL.filter { it.requiredRank.level <= rank.level }
    }
}
