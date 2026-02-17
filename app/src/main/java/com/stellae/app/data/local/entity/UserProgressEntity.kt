package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1,         // singleton row
    val xp: Long = 0,
    val rankLevel: Int = 1,
    val streakCount: Int = 0,
    val streakLastDate: String = "",      // ISO date string e.g. "2026-02-17"
    val freezePotions: Int = 1,
    val weeklyGoalDays: Int = 5,
    val totalCardsReviewed: Long = 0,
    val totalCorrect: Long = 0,
    val onboardingComplete: Boolean = false
)
