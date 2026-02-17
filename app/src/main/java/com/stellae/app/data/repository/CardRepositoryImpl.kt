package com.stellae.app.data.repository

import com.stellae.app.data.local.dao.CardDao
import com.stellae.app.data.local.dao.FsrsDao
import com.stellae.app.data.local.entity.CardEntity
import com.stellae.app.data.local.entity.FsrsDataEntity
import com.stellae.app.domain.model.Card
import com.stellae.app.domain.model.CardState
import com.stellae.app.domain.model.FsrsState
import com.stellae.app.domain.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
    private val fsrsDao: FsrsDao,
) : CardRepository {

    // ── Mapping helpers ──────────────────────────────────────────────────────

    private fun CardEntity.toDomain(): Card = Card(
        id               = id,
        factType         = factType,
        questionTemplate = questionTemplate,
        correctAnswer    = correctAnswer,
        explanation      = explanation,
        relatedPlanetId  = relatedPlanetId,
        relatedSignId    = relatedSignId,
        difficultyTier   = difficultyTier,
    )

    private fun FsrsDataEntity.toDomain(): FsrsState = FsrsState(
        cardId         = cardId,
        difficulty     = difficulty,
        stability      = stability,
        retrievability = retrievability,
        reps           = reps,
        lapses         = lapses,
        lastReview     = lastReview,
        nextReview     = nextReview,
        state          = when (state) {
            1    -> CardState.LEARNING
            2    -> CardState.REVIEW
            3    -> CardState.RELEARNING
            else -> CardState.NEW
        },
    )

    private fun FsrsState.toEntity(): FsrsDataEntity = FsrsDataEntity(
        cardId         = cardId,
        difficulty     = difficulty,
        stability      = stability,
        retrievability = retrievability,
        reps           = reps,
        lapses         = lapses,
        lastReview     = lastReview,
        nextReview     = nextReview,
        state          = when (state) {
            CardState.NEW        -> 0
            CardState.LEARNING   -> 1
            CardState.REVIEW     -> 2
            CardState.RELEARNING -> 3
        },
    )

    // ── CardRepository implementation ────────────────────────────────────────

    override fun getAllCards(): Flow<List<Card>> =
        cardDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getCardsByType(type: String): Flow<List<Card>> =
        cardDao.getByType(type).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getCardById(id: Long): Card? =
        cardDao.getById(id)?.toDomain()

    override suspend fun getDueCards(now: Long): List<FsrsState> =
        fsrsDao.getDueCards(now).first().map { it.toDomain() }

    override fun getDueCardCount(now: Long): Flow<Int> =
        fsrsDao.getDueCount(now)

    override suspend fun getNewCards(limit: Int): List<FsrsState> =
        fsrsDao.getNewCards(limit).map { it.toDomain() }

    override suspend fun updateFsrsState(state: FsrsState) =
        fsrsDao.upsert(state.toEntity())

    override suspend fun getWeakCards(limit: Int): List<FsrsState> =
        fsrsDao.getWeakCards(limit).map { it.toDomain() }

    override suspend fun getCardCount(): Int =
        cardDao.getCount()
}
