package com.stellae.app.domain.repository

import com.stellae.app.domain.model.Card
import com.stellae.app.domain.model.FsrsState
import kotlinx.coroutines.flow.Flow

interface CardRepository {
    /** Emit the full card catalogue, updating on any database change. */
    fun getAllCards(): Flow<List<Card>>

    /** Emit only cards whose [Card.factType] matches [type]. */
    fun getCardsByType(type: String): Flow<List<Card>>

    /** Return a single card by primary key, or `null` if not found. */
    suspend fun getCardById(id: Long): Card?

    /**
     * Return all [FsrsState] records whose [FsrsState.nextReview] is <= [now].
     * These are cards that are ready to be shown again.
     */
    suspend fun getDueCards(now: Long): List<FsrsState>

    /** Reactive count of cards due for review at [now]. */
    fun getDueCardCount(now: Long): Flow<Int>

    /**
     * Return up to [limit] brand-new cards (state == NEW) that have never
     * been reviewed. Used to introduce new material each session.
     */
    suspend fun getNewCards(limit: Int): List<FsrsState>

    /** Persist an updated [FsrsState] (insert or replace). */
    suspend fun updateFsrsState(state: FsrsState)

    /** Cards the user has gotten wrong, sorted by lowest stability (weakest first). */
    suspend fun getWeakCards(limit: Int): List<FsrsState>

    /** Total number of cards in the catalogue. */
    suspend fun getCardCount(): Int
}
