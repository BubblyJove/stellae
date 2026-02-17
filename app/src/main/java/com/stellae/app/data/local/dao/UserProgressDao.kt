package com.stellae.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellae.app.data.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun get(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getOnce(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET xp = xp + :amount WHERE id = 1")
    suspend fun addXp(amount: Long)

    @Query("UPDATE user_progress SET streakCount = :count, streakLastDate = :date WHERE id = 1")
    suspend fun updateStreak(count: Int, date: String)
}
