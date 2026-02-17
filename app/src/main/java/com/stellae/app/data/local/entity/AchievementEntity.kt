package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String,       // "constellation", "planet_card", "milestone"
    val unlocked: Boolean = false,
    val unlockedAt: Long = 0L,
    val progress: Float = 0f    // 0.0 to 1.0
)
