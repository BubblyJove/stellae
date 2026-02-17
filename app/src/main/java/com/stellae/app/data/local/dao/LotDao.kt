package com.stellae.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellae.app.data.local.entity.LotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LotDao {

    @Query("SELECT * FROM lots ORDER BY id")
    fun getAll(): Flow<List<LotEntity>>

    @Query("SELECT * FROM lots WHERE id = :id")
    suspend fun getById(id: Int): LotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lots: List<LotEntity>)
}
