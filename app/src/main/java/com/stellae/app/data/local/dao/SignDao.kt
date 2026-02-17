package com.stellae.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellae.app.data.local.entity.SignEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SignDao {

    @Query("SELECT * FROM signs ORDER BY id")
    fun getAll(): Flow<List<SignEntity>>

    @Query("SELECT * FROM signs WHERE id = :id")
    suspend fun getById(id: Int): SignEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(signs: List<SignEntity>)
}
