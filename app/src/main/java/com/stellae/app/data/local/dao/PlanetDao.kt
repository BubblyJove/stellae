package com.stellae.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellae.app.data.local.entity.PlanetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanetDao {

    @Query("SELECT * FROM planets ORDER BY id")
    fun getAll(): Flow<List<PlanetEntity>>

    @Query("SELECT * FROM planets WHERE id = :id")
    suspend fun getById(id: Int): PlanetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(planets: List<PlanetEntity>)
}
