package com.stellae.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.stellae.app.data.local.entity.SessionLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionLogDao {

    @Insert
    suspend fun insert(session: SessionLogEntity)

    @Query("SELECT * FROM session_log ORDER BY timestamp DESC")
    fun getAll(): Flow<List<SessionLogEntity>>

    @Query("SELECT * FROM session_log ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<SessionLogEntity>>

    @Query("SELECT SUM(cardsReviewed) FROM session_log WHERE timestamp >= :since")
    suspend fun getTotalReviewedSince(since: Long): Int?

    @Query("SELECT SUM(correctCount) FROM session_log WHERE timestamp >= :since")
    suspend fun getTotalCorrectSince(since: Long): Int?
}
