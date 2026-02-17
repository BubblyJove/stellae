package com.stellae.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lots")
data class LotEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val dayFormula: String,
    val nightFormula: String,
    val description: String
)
