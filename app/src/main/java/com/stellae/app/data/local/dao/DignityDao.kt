package com.stellae.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stellae.app.data.local.entity.DecanEntity
import com.stellae.app.data.local.entity.DomicileEntity
import com.stellae.app.data.local.entity.ExaltationEntity
import com.stellae.app.data.local.entity.TermEntity
import com.stellae.app.data.local.entity.TriplicityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DignityDao {

    // --- Domicile queries ---

    @Query("SELECT * FROM domiciles WHERE signId = :signId")
    suspend fun getDomicileForSign(signId: Int): DomicileEntity?

    @Query("SELECT * FROM domiciles WHERE planetId = :planetId")
    suspend fun getDomicilesForPlanet(planetId: Int): List<DomicileEntity>

    // --- Exaltation queries ---

    @Query("SELECT * FROM exaltations WHERE signId = :signId")
    suspend fun getExaltationForSign(signId: Int): ExaltationEntity?

    @Query("SELECT * FROM exaltations WHERE planetId = :planetId")
    suspend fun getExaltationForPlanet(planetId: Int): ExaltationEntity?

    // --- Triplicity queries ---

    @Query("SELECT * FROM triplicities WHERE element = :element")
    suspend fun getTriplicityForElement(element: String): TriplicityEntity?

    // --- Term queries ---

    @Query("SELECT * FROM terms WHERE signId = :signId ORDER BY `order`")
    suspend fun getTermsForSign(signId: Int): List<TermEntity>

    @Query("SELECT * FROM terms WHERE signId = :signId AND degreeStart <= :degree AND degreeEnd >= :degree")
    suspend fun getTermAtDegree(signId: Int, degree: Int): TermEntity?

    // --- Decan queries ---

    @Query("SELECT * FROM decans_ptolemaic WHERE signId = :signId ORDER BY decanNumber")
    suspend fun getDecansForSign(signId: Int): List<DecanEntity>

    @Query("SELECT * FROM decans_ptolemaic WHERE signId = :signId AND decanNumber = :decan")
    suspend fun getDecanForSignAndNumber(signId: Int, decan: Int): DecanEntity?

    // --- Bulk inserts ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomiciles(domiciles: List<DomicileEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExaltations(exaltations: List<ExaltationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTriplicities(triplicities: List<TriplicityEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerms(terms: List<TermEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDecans(decans: List<DecanEntity>)

    // --- Flow accessors for all rows ---

    @Query("SELECT * FROM domiciles")
    fun getAllDomiciles(): Flow<List<DomicileEntity>>

    @Query("SELECT * FROM exaltations")
    fun getAllExaltations(): Flow<List<ExaltationEntity>>

    @Query("SELECT * FROM triplicities")
    fun getAllTriplicities(): Flow<List<TriplicityEntity>>

    @Query("SELECT * FROM terms ORDER BY signId, `order`")
    fun getAllTerms(): Flow<List<TermEntity>>

    @Query("SELECT * FROM decans_ptolemaic ORDER BY signId, decanNumber")
    fun getAllDecans(): Flow<List<DecanEntity>>
}
