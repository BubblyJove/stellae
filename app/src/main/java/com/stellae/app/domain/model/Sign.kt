package com.stellae.app.domain.model

data class Sign(
    val id: Int,
    val name: String,
    val glyph: String,
    val element: Element,
    val modality: Modality,
    val degreeStart: Int,
    val degreeEnd: Int
)

enum class Element { FIRE, EARTH, AIR, WATER }
enum class Modality { CARDINAL, FIXED, MUTABLE }
