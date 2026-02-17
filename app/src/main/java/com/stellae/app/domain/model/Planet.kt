package com.stellae.app.domain.model

import androidx.compose.ui.graphics.Color

data class Planet(
    val id: Int,
    val name: String,
    val glyph: String,
    val color: Color,
    val nature: Nature,
    val sect: Sect
)

enum class Nature { BENEFIC, MALEFIC, NEUTRAL }
enum class Sect { DIURNAL, NOCTURNAL, NEUTRAL }
