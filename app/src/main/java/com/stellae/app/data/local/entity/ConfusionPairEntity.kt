package com.stellae.app.data.local.entity

import androidx.room.Entity

@Entity(tableName = "confusion_pairs", primaryKeys = ["cardAId", "cardBId"])
data class ConfusionPairEntity(
    val cardAId: Long,
    val cardBId: Long,
    val confusionCount: Int = 0,
    val lastDrilled: Long = 0L
)
