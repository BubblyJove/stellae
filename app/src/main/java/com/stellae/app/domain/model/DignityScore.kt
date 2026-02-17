package com.stellae.app.domain.model

data class DignityScore(
    val planetId: Int,
    val signId: Int,
    val degree: Int,
    val isDayChart: Boolean,
    val domicile: Boolean = false,
    val exaltation: Boolean = false,
    val triplicity: Boolean = false,
    val term: Boolean = false,
    val decan: Boolean = false,
    val detriment: Boolean = false,
    val fall: Boolean = false,
    val totalScore: Int = 0,
    val dignities: List<DignityType> = emptyList()
)
