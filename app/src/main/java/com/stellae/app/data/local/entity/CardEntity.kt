package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val factType: String,           // "domicile", "exaltation", "triplicity", "term", "decan", "lot", "scoring"
    val questionTemplate: String,
    val correctAnswer: String,
    val explanation: String,
    val relatedPlanetId: Int?,
    val relatedSignId: Int?,
    val difficultyTier: Int = 1     // 1-5 for ordering introduction
)
