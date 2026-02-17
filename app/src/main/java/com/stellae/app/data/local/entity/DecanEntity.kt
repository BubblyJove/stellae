package com.stellae.app.data.local.entity

import androidx.room.Entity

@Entity(tableName = "decans_ptolemaic", primaryKeys = ["signId", "decanNumber"])
data class DecanEntity(
    val signId: Int,
    val decanNumber: Int,       // 1, 2, 3
    val planetId: Int
)
