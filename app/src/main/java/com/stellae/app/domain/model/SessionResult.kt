package com.stellae.app.domain.model

data class SessionResult(
    val cardsReviewed: Int,
    val correctCount: Int,
    val xpEarned: Long,
    val durationSeconds: Int,
    val streakMaintained: Boolean,
    val newStreakCount: Int,
    val rankBefore: Rank,
    val rankAfter: Rank,
    val xpBefore: Long,
    val xpAfter: Long,
    val weakAreas: List<String> = emptyList(),
    val tip: String = ""
)
