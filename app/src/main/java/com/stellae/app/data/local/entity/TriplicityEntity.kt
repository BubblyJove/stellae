package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "triplicities")
data class TriplicityEntity(
    @PrimaryKey val element: String,    // "fire", "earth", "air", "water"
    val dayRulerId: Int,
    val nightRulerId: Int,
    val participatingRulerId: Int
)
