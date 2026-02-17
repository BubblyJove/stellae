package com.stellae.app.data.local.entity

import androidx.room.Entity

@Entity(tableName = "terms", primaryKeys = ["signId", "order"])
data class TermEntity(
    val signId: Int,
    val planetId: Int,
    val degreeStart: Int,
    val degreeEnd: Int,
    val order: Int              // 1-5
)
