package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_log")
data class SessionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val cardsReviewed: Int,
    val correctCount: Int,
    val xpEarned: Long,
    val durationSeconds: Int
)
