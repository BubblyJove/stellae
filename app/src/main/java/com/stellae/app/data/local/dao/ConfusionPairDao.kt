package com.stellae.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellae.app.data.local.entity.ConfusionPairEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfusionPairDao {

    @Query("SELECT * FROM confusion_pairs WHERE confusionCount > 0 ORDER BY confusionCount DESC")
    fun getTopConfusions(): Flow<List<ConfusionPairEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pair: ConfusionPairEntity)

    @Query("SELECT * FROM confusion_pairs WHERE (cardAId = :cardId OR cardBId = :cardId)")
    suspend fun getForCard(cardId: Long): List<ConfusionPairEntity>
}
