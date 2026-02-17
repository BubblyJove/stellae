package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exaltations")
data class ExaltationEntity(
    @PrimaryKey val planetId: Int,
    val signId: Int,
    val degree: Int
)
