package com.stellae.app.data.repository

import androidx.compose.ui.graphics.Color
import com.stellae.app.data.local.dao.DignityDao
import com.stellae.app.data.local.dao.PlanetDao
import com.stellae.app.data.local.dao.SignDao
import com.stellae.app.data.local.entity.PlanetEntity
import com.stellae.app.data.local.entity.SignEntity
import com.stellae.app.data.local.entity.TermEntity
import com.stellae.app.domain.model.Element
import com.stellae.app.domain.model.Modality
import com.stellae.app.domain.model.Nature
import com.stellae.app.domain.model.Planet
import com.stellae.app.domain.model.Sect
import com.stellae.app.domain.model.Sign
import com.stellae.app.domain.repository.DignityRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DignityRepositoryImpl @Inject constructor(
    private val planetDao: PlanetDao,
    private val signDao: SignDao,
    private val dignityDao: DignityDao,
) : DignityRepository {

    // ── Mapping helpers ──────────────────────────────────────────────────────

    /**
     * Parse a hex color string such as "#D4A832" into a Compose [Color].
     * Falls back to [Color.White] on any malformed input.
     */
    private fun String.toComposeColor(): Color = try {
        val hex = removePrefix("#")
        val argb = when (hex.length) {
            6    -> "FF$hex"
            8    -> hex
            else -> "FFFFFFFF"
        }
        Color(java.lang.Long.parseLong(argb, 16).toInt())
    } catch (_: Exception) {
        Color.White
    }

    private fun PlanetEntity.toDomain(): Planet = Planet(
        id     = id,
        name   = name,
        glyph  = glyph,
        color  = colorHex.toComposeColor(),
        nature = when (nature.lowercase()) {
            "benefic"  -> Nature.BENEFIC
            "malefic"  -> Nature.MALEFIC
            else       -> Nature.NEUTRAL
        },
        sect = when (sect.lowercase()) {
            "diurnal"  -> Sect.DIURNAL
            "nocturnal" -> Sect.NOCTURNAL
            else        -> Sect.NEUTRAL
        },
    )

    private fun SignEntity.toDomain(): Sign = Sign(
        id          = id,
        name        = name,
        glyph       = glyph,
        element     = when (element.lowercase()) {
            "fire"  -> Element.FIRE
            "earth" -> Element.EARTH
            "air"   -> Element.AIR
            else    -> Element.WATER
        },
        modality    = when (modality.lowercase()) {
            "cardinal" -> Modality.CARDINAL
            "fixed"    -> Modality.FIXED
            else       -> Modality.MUTABLE
        },
        degreeStart = degreeStart,
        degreeEnd   = degreeEnd,
    )

    // ── DignityRepository implementation ────────────────────────────────────

    // --- Domicile ---

    override suspend fun getDomicileRuler(signId: Int): Planet? {
        val entity = dignityDao.getDomicileForSign(signId) ?: return null
        return planetDao.getById(entity.planetId)?.toDomain()
    }

    override suspend fun getDomicileSigns(planetId: Int): List<Sign> {
        val entities = dignityDao.getDomicilesForPlanet(planetId)
        return entities.mapNotNull { signDao.getById(it.signId)?.toDomain() }
    }

    // --- Exaltation ---

    override suspend fun getExaltation(planetId: Int): Pair<Sign, Int>? {
        val entity = dignityDao.getExaltationForPlanet(planetId) ?: return null
        val sign   = signDao.getById(entity.signId)?.toDomain() ?: return null
        return Pair(sign, entity.degree)
    }

    override suspend fun getExaltedPlanet(signId: Int): Planet? {
        val entity = dignityDao.getExaltationForSign(signId) ?: return null
        return planetDao.getById(entity.planetId)?.toDomain()
    }

    // --- Triplicity ---

    override suspend fun getTriplicityRulers(element: Element, isDayChart: Boolean): Planet? {
        val elementStr = element.name.lowercase()
        val entity = dignityDao.getTriplicityForElement(elementStr) ?: return null
        val planetId = if (isDayChart) entity.dayRulerId else entity.nightRulerId
        return planetDao.getById(planetId)?.toDomain()
    }

    // --- Terms ---

    override suspend fun getTermRuler(signId: Int, degree: Int): Planet? {
        val entity = dignityDao.getTermAtDegree(signId, degree) ?: return null
        return planetDao.getById(entity.planetId)?.toDomain()
    }

    override suspend fun getTermsForSign(signId: Int): List<Pair<Planet, IntRange>> {
        val terms: List<TermEntity> = dignityDao.getTermsForSign(signId)
        return terms.mapNotNull { term ->
            val planet = planetDao.getById(term.planetId)?.toDomain() ?: return@mapNotNull null
            Pair(planet, term.degreeStart..term.degreeEnd)
        }
    }

    // --- Decans ---

    override suspend fun getDecanRuler(signId: Int, degree: Int): Planet? {
        // Decan number: degrees 0-9 → 1, 10-19 → 2, 20-29 → 3.
        val decanNumber = (degree.coerceIn(0, 29) / 10) + 1
        val entity = dignityDao.getDecanForSignAndNumber(signId, decanNumber) ?: return null
        return planetDao.getById(entity.planetId)?.toDomain()
    }

    // --- Reference data ---

    override suspend fun getAllPlanets(): List<Planet> =
        planetDao.getAll().first().map { it.toDomain() }

    override suspend fun getAllSigns(): List<Sign> =
        signDao.getAll().first().map { it.toDomain() }

    override suspend fun getPlanetById(id: Int): Planet? =
        planetDao.getById(id)?.toDomain()

    override suspend fun getSignById(id: Int): Sign? =
        signDao.getById(id)?.toDomain()
}
