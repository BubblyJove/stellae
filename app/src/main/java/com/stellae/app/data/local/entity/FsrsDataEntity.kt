package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fsrs_data")
data class FsrsDataEntity(
    @PrimaryKey val cardId: Long,
    val difficulty: Float = 0.3f,
    val stability: Float = 0f,
    val retrievability: Float = 1.0f,
    val reps: Int = 0,
    val lapses: Int = 0,
    val lastReview: Long = 0L,       // epoch millis
    val nextReview: Long = 0L,
    val state: Int = 0               // 0=new, 1=learning, 2=review, 3=relearning
)
