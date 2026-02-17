package com.stellae.app.domain.model

data class QuizQuestion(
    val card: Card,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val dignityTypeLabel: String,
    val questionType: QuestionType
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    REVERSE_MULTIPLE_CHOICE,
    SPEED_ROUND,
    WHEEL_TAP,
    TYPE_ANSWER,
    COMPOSITE_SCORING,
    LOT_CALCULATION
}
