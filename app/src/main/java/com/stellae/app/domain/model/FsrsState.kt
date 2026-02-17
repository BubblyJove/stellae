package com.stellae.app.domain.model

data class FsrsState(
    val cardId: Long,
    val difficulty: Float = 0.3f,
    val stability: Float = 0f,
    val retrievability: Float = 1.0f,
    val reps: Int = 0,
    val lapses: Int = 0,
    val lastReview: Long = 0L,
    val nextReview: Long = 0L,
    val state: CardState = CardState.NEW
)

enum class CardState { NEW, LEARNING, REVIEW, RELEARNING }

enum class Rating(val value: Int) {
    AGAIN(1),
    HARD(2),
    GOOD(3),
    EASY(4)
}
