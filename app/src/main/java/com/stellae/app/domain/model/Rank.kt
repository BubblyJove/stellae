package com.stellae.app.domain.model

enum class Rank(
    val level: Int,
    val title: String,
    val xpRequired: Long,
    val unlockDescription: String
) {
    NEOPHYTE(1, "Neophyte", 0, "Complete onboarding"),
    STUDENT(2, "Student of the Stars", 500, "Master all domiciles"),
    ACOLYTE(3, "Acolyte", 1500, "Master all exaltations"),
    ADEPT(4, "Adept", 3500, "Master triplicity rulers"),
    PRACTITIONER(5, "Practitioner", 7000, "Master Egyptian terms"),
    SCHOLAR(6, "Scholar", 12000, "Master Ptolemaic decans"),
    INTERPRETER(7, "Interpreter", 20000, "Master composite dignity scoring"),
    SAGE(8, "Sage", 35000, "Master lot calculations"),
    MASTER_ASTROLOGER(9, "Master Astrologer", 55000, "Sub-5-second dignity assessment"),
    MAGISTER_STELLARUM(10, "Magister Stellarum", 80000, "Complete all achievements, 95%+ accuracy");

    companion object {
        fun fromXp(xp: Long): Rank = entries.last { xp >= it.xpRequired }
        fun fromLevel(level: Int): Rank = entries.first { it.level == level }
    }
}
