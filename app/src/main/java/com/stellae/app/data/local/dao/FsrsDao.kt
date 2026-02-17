package com.stellae.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellae.app.data.local.entity.FsrsDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FsrsDao {

    @Query("SELECT * FROM fsrs_data WHERE cardId = :cardId")
    suspend fun getForCard(cardId: Long): FsrsDataEntity?

    @Query("SELECT * FROM fsrs_data WHERE nextReview <= :now AND state != 0 ORDER BY nextReview ASC")
    fun getDueCards(now: Long): Flow<List<FsrsDataEntity>>

    @Query("SELECT COUNT(*) FROM fsrs_data WHERE nextReview <= :now AND state != 0")
    fun getDueCount(now: Long): Flow<Int>

    @Query("SELECT * FROM fsrs_data WHERE state = 0 ORDER BY cardId ASC LIMIT :limit")
    suspend fun getNewCards(limit: Int): List<FsrsDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(data: FsrsDataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(data: List<FsrsDataEntity>)

    @Query("SELECT COUNT(*) FROM fsrs_data WHERE state = 0")
    suspend fun getNewCardCount(): Int

    /** Cards the user has gotten wrong (lapses > 0), weakest first. */
    @Query("SELECT * FROM fsrs_data WHERE lapses > 0 ORDER BY stability ASC LIMIT :limit")
    suspend fun getWeakCards(limit: Int): List<FsrsDataEntity>
}
