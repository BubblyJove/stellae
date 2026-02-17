package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planets")
data class PlanetEntity(
    @PrimaryKey val id: Int,
    val name: String,         // "Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter", "Saturn"
    val glyph: String,        // "☉", "☽", "☿", "♀", "♂", "♃", "♄"
    val colorHex: String,     // "#D4A832", "#B8C4D8", etc.
    val nature: String,       // "benefic", "malefic", "neutral"
    val sect: String          // "diurnal", "nocturnal", "neutral"
)
