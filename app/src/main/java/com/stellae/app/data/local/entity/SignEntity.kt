package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signs")
data class SignEntity(
    @PrimaryKey val id: Int,
    val name: String,          // "Aries" through "Pisces"
    val glyph: String,         // "♈" through "♓"
    val element: String,       // "fire", "earth", "air", "water"
    val modality: String,      // "cardinal", "fixed", "mutable"
    val degreeStart: Int,      // 0, 30, 60, ... 330
    val degreeEnd: Int         // 29, 59, 89, ... 359
)
