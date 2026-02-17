package com.stellae.app.data.local.entity

import androidx.room.Entity

@Entity(tableName = "domiciles", primaryKeys = ["planetId", "signId"])
data class DomicileEntity(
    val planetId: Int,
    val signId: Int
)
