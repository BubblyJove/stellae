package com.stellae.app.domain.model

data class Card(
    val id: Long,
    val factType: String,
    val questionTemplate: String,
    val correctAnswer: String,
    val explanation: String,
    val relatedPlanetId: Int?,
    val relatedSignId: Int?,
    val difficultyTier: Int
)
