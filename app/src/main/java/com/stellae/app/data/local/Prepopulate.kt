package com.stellae.app.data.local

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.stellae.app.data.local.entity.AchievementEntity
import com.stellae.app.data.local.entity.DecanEntity
import com.stellae.app.data.local.entity.DomicileEntity
import com.stellae.app.data.local.entity.ExaltationEntity
import com.stellae.app.data.local.entity.LotEntity
import com.stellae.app.data.local.entity.PlanetEntity
import com.stellae.app.data.local.entity.SignEntity
import com.stellae.app.data.local.entity.TermEntity
import com.stellae.app.data.local.entity.TriplicityEntity
import com.stellae.app.data.local.entity.UserProgressEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

/**
 * RoomDatabase.Callback that seeds all static astrological reference data when the database
 * is first created. Uses a Provider<StellaeDatabase> to avoid circular dependency with the
 * database builder.
 */
class PrepopulateCallback(
    private val databaseProvider: Provider<StellaeDatabase>
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = databaseProvider.get()
            insertPlanets(database)
            insertSigns(database)
            insertDomiciles(database)
            insertExaltations(database)
            insertTriplicities(database)
            insertTerms(database)
            insertDecans(database)
            insertLots(database)
            insertUserProgress(database)
            insertAchievements(database)
        }
    }

    // -------------------------------------------------------------------------
    // Planets
    // -------------------------------------------------------------------------

    private suspend fun insertPlanets(db: StellaeDatabase) {
        db.planetDao().insertAll(
            listOf(
                PlanetEntity(id = 1, name = "Sun",     glyph = "\u2609", colorHex = "#D4A832", nature = "benefic",  sect = "diurnal"),
                PlanetEntity(id = 2, name = "Moon",    glyph = "\u263D", colorHex = "#B8C4D8", nature = "benefic",  sect = "nocturnal"),
                PlanetEntity(id = 3, name = "Mercury", glyph = "\u263F", colorHex = "#C99A45", nature = "neutral",  sect = "neutral"),
                PlanetEntity(id = 4, name = "Venus",   glyph = "\u2640", colorHex = "#4CA77A", nature = "benefic",  sect = "nocturnal"),
                PlanetEntity(id = 5, name = "Mars",    glyph = "\u2642", colorHex = "#C44545", nature = "malefic",  sect = "nocturnal"),
                PlanetEntity(id = 6, name = "Jupiter", glyph = "\u2643", colorHex = "#4A6FA5", nature = "benefic",  sect = "diurnal"),
                PlanetEntity(id = 7, name = "Saturn",  glyph = "\u2644", colorHex = "#7A8899", nature = "malefic",  sect = "diurnal")
            )
        )
    }

    // -------------------------------------------------------------------------
    // Signs
    // -------------------------------------------------------------------------

    private suspend fun insertSigns(db: StellaeDatabase) {
        db.signDao().insertAll(
            listOf(
                SignEntity(id =  1, name = "Aries",       glyph = "\u2648", element = "fire",   modality = "cardinal", degreeStart =   0, degreeEnd =  29),
                SignEntity(id =  2, name = "Taurus",      glyph = "\u2649", element = "earth",  modality = "fixed",    degreeStart =  30, degreeEnd =  59),
                SignEntity(id =  3, name = "Gemini",      glyph = "\u264A", element = "air",    modality = "mutable",  degreeStart =  60, degreeEnd =  89),
                SignEntity(id =  4, name = "Cancer",      glyph = "\u264B", element = "water",  modality = "cardinal", degreeStart =  90, degreeEnd = 119),
                SignEntity(id =  5, name = "Leo",         glyph = "\u264C", element = "fire",   modality = "fixed",    degreeStart = 120, degreeEnd = 149),
                SignEntity(id =  6, name = "Virgo",       glyph = "\u264D", element = "earth",  modality = "mutable",  degreeStart = 150, degreeEnd = 179),
                SignEntity(id =  7, name = "Libra",       glyph = "\u264E", element = "air",    modality = "cardinal", degreeStart = 180, degreeEnd = 209),
                SignEntity(id =  8, name = "Scorpio",     glyph = "\u264F", element = "water",  modality = "fixed",    degreeStart = 210, degreeEnd = 239),
                SignEntity(id =  9, name = "Sagittarius", glyph = "\u2650", element = "fire",   modality = "mutable",  degreeStart = 240, degreeEnd = 269),
                SignEntity(id = 10, name = "Capricorn",   glyph = "\u2651", element = "earth",  modality = "cardinal", degreeStart = 270, degreeEnd = 299),
                SignEntity(id = 11, name = "Aquarius",    glyph = "\u2652", element = "air",    modality = "fixed",    degreeStart = 300, degreeEnd = 329),
                SignEntity(id = 12, name = "Pisces",      glyph = "\u2653", element = "water",  modality = "mutable",  degreeStart = 330, degreeEnd = 359)
            )
        )
    }

    // -------------------------------------------------------------------------
    // Domiciles
    // Traditional rulerships: Mars rules Aries & Scorpio, Venus rules Taurus & Libra,
    // Mercury rules Gemini & Virgo, Moon rules Cancer, Sun rules Leo,
    // Jupiter rules Sagittarius & Pisces, Saturn rules Capricorn & Aquarius
    // -------------------------------------------------------------------------

    private suspend fun insertDomiciles(db: StellaeDatabase) {
        db.dignityDao().insertDomiciles(
            listOf(
                DomicileEntity(planetId = 5, signId =  1),  // Mars   → Aries
                DomicileEntity(planetId = 4, signId =  2),  // Venus  → Taurus
                DomicileEntity(planetId = 3, signId =  3),  // Mercury→ Gemini
                DomicileEntity(planetId = 2, signId =  4),  // Moon   → Cancer
                DomicileEntity(planetId = 1, signId =  5),  // Sun    → Leo
                DomicileEntity(planetId = 3, signId =  6),  // Mercury→ Virgo
                DomicileEntity(planetId = 4, signId =  7),  // Venus  → Libra
                DomicileEntity(planetId = 5, signId =  8),  // Mars   → Scorpio
                DomicileEntity(planetId = 6, signId =  9),  // Jupiter→ Sagittarius
                DomicileEntity(planetId = 7, signId = 10),  // Saturn → Capricorn
                DomicileEntity(planetId = 7, signId = 11),  // Saturn → Aquarius
                DomicileEntity(planetId = 6, signId = 12)   // Jupiter→ Pisces
            )
        )
    }

    // -------------------------------------------------------------------------
    // Exaltations
    // Sun 19° Aries, Moon 3° Taurus, Mercury 15° Virgo, Venus 27° Pisces,
    // Mars 28° Capricorn, Jupiter 15° Cancer, Saturn 21° Libra
    // -------------------------------------------------------------------------

    private suspend fun insertExaltations(db: StellaeDatabase) {
        db.dignityDao().insertExaltations(
            listOf(
                ExaltationEntity(planetId = 1, signId =  1, degree = 19),  // Sun    → Aries 19°
                ExaltationEntity(planetId = 2, signId =  2, degree =  3),  // Moon   → Taurus 3°
                ExaltationEntity(planetId = 3, signId =  6, degree = 15),  // Mercury→ Virgo 15°
                ExaltationEntity(planetId = 4, signId = 12, degree = 27),  // Venus  → Pisces 27°
                ExaltationEntity(planetId = 5, signId = 10, degree = 28),  // Mars   → Capricorn 28°
                ExaltationEntity(planetId = 6, signId =  4, degree = 15),  // Jupiter→ Cancer 15°
                ExaltationEntity(planetId = 7, signId =  7, degree = 21)   // Saturn → Libra 21°
            )
        )
    }

    // -------------------------------------------------------------------------
    // Triplicities (Dorothean/traditional sect-based)
    // fire:  day=Sun(1), night=Jupiter(6), participating=Saturn(7)
    // earth: day=Venus(4), night=Moon(2), participating=Mars(5)
    // air:   day=Saturn(7), night=Mercury(3), participating=Jupiter(6)
    // water: day=Venus(4), night=Mars(5), participating=Moon(2)
    // -------------------------------------------------------------------------

    private suspend fun insertTriplicities(db: StellaeDatabase) {
        db.dignityDao().insertTriplicities(
            listOf(
                TriplicityEntity(element = "fire",  dayRulerId = 1, nightRulerId = 6, participatingRulerId = 7),
                TriplicityEntity(element = "earth", dayRulerId = 4, nightRulerId = 2, participatingRulerId = 5),
                TriplicityEntity(element = "air",   dayRulerId = 7, nightRulerId = 3, participatingRulerId = 6),
                TriplicityEntity(element = "water", dayRulerId = 4, nightRulerId = 5, participatingRulerId = 2)
            )
        )
    }

    // -------------------------------------------------------------------------
    // Egyptian Terms
    // Each sign has 5 terms ordered 1-5 with degreeStart/degreeEnd inclusive.
    // Planet IDs: Sun=1, Moon=2, Mercury=3, Venus=4, Mars=5, Jupiter=6, Saturn=7
    // -------------------------------------------------------------------------

    private suspend fun insertTerms(db: StellaeDatabase) {
        db.dignityDao().insertTerms(buildTerms())
    }

    @Suppress("LongMethod")
    private fun buildTerms(): List<TermEntity> = listOf(
        // Aries (signId=1): Jupiter 0-5, Venus 6-11, Mercury 12-19, Mars 20-24, Saturn 25-29
        TermEntity(signId = 1, planetId = 6, degreeStart =  0, degreeEnd =  5, order = 1),
        TermEntity(signId = 1, planetId = 4, degreeStart =  6, degreeEnd = 11, order = 2),
        TermEntity(signId = 1, planetId = 3, degreeStart = 12, degreeEnd = 19, order = 3),
        TermEntity(signId = 1, planetId = 5, degreeStart = 20, degreeEnd = 24, order = 4),
        TermEntity(signId = 1, planetId = 7, degreeStart = 25, degreeEnd = 29, order = 5),

        // Taurus (signId=2): Venus 0-7, Mercury 8-13, Jupiter 14-21, Saturn 22-26, Mars 27-29
        TermEntity(signId = 2, planetId = 4, degreeStart =  0, degreeEnd =  7, order = 1),
        TermEntity(signId = 2, planetId = 3, degreeStart =  8, degreeEnd = 13, order = 2),
        TermEntity(signId = 2, planetId = 6, degreeStart = 14, degreeEnd = 21, order = 3),
        TermEntity(signId = 2, planetId = 7, degreeStart = 22, degreeEnd = 26, order = 4),
        TermEntity(signId = 2, planetId = 5, degreeStart = 27, degreeEnd = 29, order = 5),

        // Gemini (signId=3): Mercury 0-5, Jupiter 6-11, Venus 12-16, Mars 17-23, Saturn 24-29
        TermEntity(signId = 3, planetId = 3, degreeStart =  0, degreeEnd =  5, order = 1),
        TermEntity(signId = 3, planetId = 6, degreeStart =  6, degreeEnd = 11, order = 2),
        TermEntity(signId = 3, planetId = 4, degreeStart = 12, degreeEnd = 16, order = 3),
        TermEntity(signId = 3, planetId = 5, degreeStart = 17, degreeEnd = 23, order = 4),
        TermEntity(signId = 3, planetId = 7, degreeStart = 24, degreeEnd = 29, order = 5),

        // Cancer (signId=4): Mars 0-6, Jupiter 7-12, Mercury 13-18, Venus 19-25, Saturn 26-29
        TermEntity(signId = 4, planetId = 5, degreeStart =  0, degreeEnd =  6, order = 1),
        TermEntity(signId = 4, planetId = 6, degreeStart =  7, degreeEnd = 12, order = 2),
        TermEntity(signId = 4, planetId = 3, degreeStart = 13, degreeEnd = 18, order = 3),
        TermEntity(signId = 4, planetId = 4, degreeStart = 19, degreeEnd = 25, order = 4),
        TermEntity(signId = 4, planetId = 7, degreeStart = 26, degreeEnd = 29, order = 5),

        // Leo (signId=5): Jupiter 0-5, Venus 6-10, Saturn 11-17, Mercury 18-23, Mars 24-29
        TermEntity(signId = 5, planetId = 6, degreeStart =  0, degreeEnd =  5, order = 1),
        TermEntity(signId = 5, planetId = 4, degreeStart =  6, degreeEnd = 10, order = 2),
        TermEntity(signId = 5, planetId = 7, degreeStart = 11, degreeEnd = 17, order = 3),
        TermEntity(signId = 5, planetId = 3, degreeStart = 18, degreeEnd = 23, order = 4),
        TermEntity(signId = 5, planetId = 5, degreeStart = 24, degreeEnd = 29, order = 5),

        // Virgo (signId=6): Mercury 0-6, Venus 7-12, Jupiter 13-17, Mars 18-20, Saturn 21-29
        TermEntity(signId = 6, planetId = 3, degreeStart =  0, degreeEnd =  6, order = 1),
        TermEntity(signId = 6, planetId = 4, degreeStart =  7, degreeEnd = 12, order = 2),
        TermEntity(signId = 6, planetId = 6, degreeStart = 13, degreeEnd = 17, order = 3),
        TermEntity(signId = 6, planetId = 5, degreeStart = 18, degreeEnd = 20, order = 4),
        TermEntity(signId = 6, planetId = 7, degreeStart = 21, degreeEnd = 29, order = 5),

        // Libra (signId=7): Saturn 0-5, Mercury 6-13, Jupiter 14-20, Venus 21-27, Mars 28-29
        TermEntity(signId = 7, planetId = 7, degreeStart =  0, degreeEnd =  5, order = 1),
        TermEntity(signId = 7, planetId = 3, degreeStart =  6, degreeEnd = 13, order = 2),
        TermEntity(signId = 7, planetId = 6, degreeStart = 14, degreeEnd = 20, order = 3),
        TermEntity(signId = 7, planetId = 4, degreeStart = 21, degreeEnd = 27, order = 4),
        TermEntity(signId = 7, planetId = 5, degreeStart = 28, degreeEnd = 29, order = 5),

        // Scorpio (signId=8): Mars 0-6, Venus 7-10, Mercury 11-18, Jupiter 19-23, Saturn 24-29
        TermEntity(signId = 8, planetId = 5, degreeStart =  0, degreeEnd =  6, order = 1),
        TermEntity(signId = 8, planetId = 4, degreeStart =  7, degreeEnd = 10, order = 2),
        TermEntity(signId = 8, planetId = 3, degreeStart = 11, degreeEnd = 18, order = 3),
        TermEntity(signId = 8, planetId = 6, degreeStart = 19, degreeEnd = 23, order = 4),
        TermEntity(signId = 8, planetId = 7, degreeStart = 24, degreeEnd = 29, order = 5),

        // Sagittarius (signId=9): Jupiter 0-11, Venus 12-16, Mercury 17-20, Saturn 21-25, Mars 26-29
        TermEntity(signId = 9, planetId = 6, degreeStart =  0, degreeEnd = 11, order = 1),
        TermEntity(signId = 9, planetId = 4, degreeStart = 12, degreeEnd = 16, order = 2),
        TermEntity(signId = 9, planetId = 3, degreeStart = 17, degreeEnd = 20, order = 3),
        TermEntity(signId = 9, planetId = 7, degreeStart = 21, degreeEnd = 25, order = 4),
        TermEntity(signId = 9, planetId = 5, degreeStart = 26, degreeEnd = 29, order = 5),

        // Capricorn (signId=10): Mercury 0-6, Jupiter 7-11, Venus 12-18, Saturn 19-24, Mars 25-29
        TermEntity(signId = 10, planetId = 3, degreeStart =  0, degreeEnd =  6, order = 1),
        TermEntity(signId = 10, planetId = 6, degreeStart =  7, degreeEnd = 11, order = 2),
        TermEntity(signId = 10, planetId = 4, degreeStart = 12, degreeEnd = 18, order = 3),
        TermEntity(signId = 10, planetId = 7, degreeStart = 19, degreeEnd = 24, order = 4),
        TermEntity(signId = 10, planetId = 5, degreeStart = 25, degreeEnd = 29, order = 5),

        // Aquarius (signId=11): Mercury 0-6, Venus 7-11, Jupiter 12-19, Mars 20-24, Saturn 25-29
        TermEntity(signId = 11, planetId = 3, degreeStart =  0, degreeEnd =  6, order = 1),
        TermEntity(signId = 11, planetId = 4, degreeStart =  7, degreeEnd = 11, order = 2),
        TermEntity(signId = 11, planetId = 6, degreeStart = 12, degreeEnd = 19, order = 3),
        TermEntity(signId = 11, planetId = 5, degreeStart = 20, degreeEnd = 24, order = 4),
        TermEntity(signId = 11, planetId = 7, degreeStart = 25, degreeEnd = 29, order = 5),

        // Pisces (signId=12): Venus 0-11, Jupiter 12-15, Mercury 16-18, Mars 19-27, Saturn 28-29
        TermEntity(signId = 12, planetId = 4, degreeStart =  0, degreeEnd = 11, order = 1),
        TermEntity(signId = 12, planetId = 6, degreeStart = 12, degreeEnd = 15, order = 2),
        TermEntity(signId = 12, planetId = 3, degreeStart = 16, degreeEnd = 18, order = 3),
        TermEntity(signId = 12, planetId = 5, degreeStart = 19, degreeEnd = 27, order = 4),
        TermEntity(signId = 12, planetId = 7, degreeStart = 28, degreeEnd = 29, order = 5)
    )

    // -------------------------------------------------------------------------
    // Ptolemaic Decans
    // decanNumber 1=0-9°, 2=10-19°, 3=20-29°
    // -------------------------------------------------------------------------

    private suspend fun insertDecans(db: StellaeDatabase) {
        db.dignityDao().insertDecans(buildDecans())
    }

    private fun buildDecans(): List<DecanEntity> = listOf(
        // Aries (signId=1): Mars, Sun, Jupiter
        DecanEntity(signId =  1, decanNumber = 1, planetId = 5),
        DecanEntity(signId =  1, decanNumber = 2, planetId = 1),
        DecanEntity(signId =  1, decanNumber = 3, planetId = 6),

        // Taurus (signId=2): Venus, Moon, Saturn
        DecanEntity(signId =  2, decanNumber = 1, planetId = 4),
        DecanEntity(signId =  2, decanNumber = 2, planetId = 2),
        DecanEntity(signId =  2, decanNumber = 3, planetId = 7),

        // Gemini (signId=3): Mercury, Venus, Saturn
        DecanEntity(signId =  3, decanNumber = 1, planetId = 3),
        DecanEntity(signId =  3, decanNumber = 2, planetId = 4),
        DecanEntity(signId =  3, decanNumber = 3, planetId = 7),

        // Cancer (signId=4): Moon, Mars, Jupiter
        DecanEntity(signId =  4, decanNumber = 1, planetId = 2),
        DecanEntity(signId =  4, decanNumber = 2, planetId = 5),
        DecanEntity(signId =  4, decanNumber = 3, planetId = 6),

        // Leo (signId=5): Sun, Jupiter, Mars
        DecanEntity(signId =  5, decanNumber = 1, planetId = 1),
        DecanEntity(signId =  5, decanNumber = 2, planetId = 6),
        DecanEntity(signId =  5, decanNumber = 3, planetId = 5),

        // Virgo (signId=6): Mercury, Saturn, Venus
        DecanEntity(signId =  6, decanNumber = 1, planetId = 3),
        DecanEntity(signId =  6, decanNumber = 2, planetId = 7),
        DecanEntity(signId =  6, decanNumber = 3, planetId = 4),

        // Libra (signId=7): Venus, Mercury, Jupiter
        DecanEntity(signId =  7, decanNumber = 1, planetId = 4),
        DecanEntity(signId =  7, decanNumber = 2, planetId = 3),
        DecanEntity(signId =  7, decanNumber = 3, planetId = 6),

        // Scorpio (signId=8): Mars, Venus, Moon
        DecanEntity(signId =  8, decanNumber = 1, planetId = 5),
        DecanEntity(signId =  8, decanNumber = 2, planetId = 4),
        DecanEntity(signId =  8, decanNumber = 3, planetId = 2),

        // Sagittarius (signId=9): Jupiter, Mars, Sun
        DecanEntity(signId =  9, decanNumber = 1, planetId = 6),
        DecanEntity(signId =  9, decanNumber = 2, planetId = 5),
        DecanEntity(signId =  9, decanNumber = 3, planetId = 1),

        // Capricorn (signId=10): Saturn, Venus, Mercury
        DecanEntity(signId = 10, decanNumber = 1, planetId = 7),
        DecanEntity(signId = 10, decanNumber = 2, planetId = 4),
        DecanEntity(signId = 10, decanNumber = 3, planetId = 3),

        // Aquarius (signId=11): Saturn, Mercury, Venus
        DecanEntity(signId = 11, decanNumber = 1, planetId = 7),
        DecanEntity(signId = 11, decanNumber = 2, planetId = 3),
        DecanEntity(signId = 11, decanNumber = 3, planetId = 4),

        // Pisces (signId=12): Jupiter, Moon, Mars
        DecanEntity(signId = 12, decanNumber = 1, planetId = 6),
        DecanEntity(signId = 12, decanNumber = 2, planetId = 2),
        DecanEntity(signId = 12, decanNumber = 3, planetId = 5)
    )

    // -------------------------------------------------------------------------
    // Lots (Arabic Parts / Hermetic Lots)
    // -------------------------------------------------------------------------

    private suspend fun insertLots(db: StellaeDatabase) {
        db.lotDao().insertAll(
            listOf(
                LotEntity(
                    id = 1,
                    name = "Lot of Fortune",
                    dayFormula = "Asc + Moon - Sun",
                    nightFormula = "Asc + Sun - Moon",
                    description = "The most important Arabic lot, representing the body, " +
                        "material fortune, and the overall condition of the native's life. " +
                        "Associated with the Moon's sect."
                ),
                LotEntity(
                    id = 2,
                    name = "Lot of Spirit",
                    dayFormula = "Asc + Sun - Moon",
                    nightFormula = "Asc + Moon - Sun",
                    description = "Counterpart to the Lot of Fortune, representing the soul, " +
                        "intention, and the Sun's sect. Indicates matters of will and purpose."
                ),
                LotEntity(
                    id = 3,
                    name = "Lot of Eros",
                    dayFormula = "Asc + Venus - Spirit",
                    nightFormula = "Asc + Spirit - Venus",
                    description = "Related to desire, love, and things we are drawn toward. " +
                        "Shows the nature of one's erotic and creative impulses."
                ),
                LotEntity(
                    id = 4,
                    name = "Lot of Necessity",
                    dayFormula = "Asc + Fortune - Mercury",
                    nightFormula = "Asc + Mercury - Fortune",
                    description = "Related to constraint, compulsion, and unavoidable " +
                        "circumstances. Indicates areas where the native has little choice."
                ),
                LotEntity(
                    id = 5,
                    name = "Lot of Courage",
                    dayFormula = "Asc + Fortune - Mars",
                    nightFormula = "Asc + Mars - Fortune",
                    description = "Related to boldness, daring, and the capacity for action. " +
                        "Shows where the native can draw on martial energy constructively."
                ),
                LotEntity(
                    id = 6,
                    name = "Lot of Victory",
                    dayFormula = "Asc + Jupiter - Spirit",
                    nightFormula = "Asc + Spirit - Jupiter",
                    description = "Related to success, recognition, and Jupiterian expansion. " +
                        "Indicates where the native may find good fortune and advancement."
                ),
                LotEntity(
                    id = 7,
                    name = "Lot of Nemesis",
                    dayFormula = "Asc + Fortune - Saturn",
                    nightFormula = "Asc + Saturn - Fortune",
                    description = "Related to fate, retribution, and Saturnian limitation. " +
                        "Indicates areas of difficulty, karmic debt, or inevitable reversal."
                )
            )
        )
    }

    // -------------------------------------------------------------------------
    // User Progress — singleton row with default values
    // -------------------------------------------------------------------------

    private suspend fun insertUserProgress(db: StellaeDatabase) {
        db.userProgressDao().upsert(UserProgressEntity())
    }

    // -------------------------------------------------------------------------
    // Achievements — constellation badges, planet mastery cards, milestones
    // -------------------------------------------------------------------------

    private suspend fun insertAchievements(db: StellaeDatabase) {
        db.achievementDao().upsertAll(buildAchievements())
    }

    @Suppress("LongMethod")
    private fun buildAchievements(): List<AchievementEntity> = listOf(

        // --- Constellation badges: one per zodiac sign ---
        AchievementEntity(
            id = "constellation_aries",
            name = "Ram's Horn",
            description = "Master all dignity facts for Aries.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_taurus",
            name = "Bull's Eye",
            description = "Master all dignity facts for Taurus.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_gemini",
            name = "Twin Stars",
            description = "Master all dignity facts for Gemini.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_cancer",
            name = "Moonlit Shore",
            description = "Master all dignity facts for Cancer.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_leo",
            name = "Solar Crown",
            description = "Master all dignity facts for Leo.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_virgo",
            name = "Harvest Wreath",
            description = "Master all dignity facts for Virgo.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_libra",
            name = "Balanced Scales",
            description = "Master all dignity facts for Libra.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_scorpio",
            name = "Scorpion's Sting",
            description = "Master all dignity facts for Scorpio.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_sagittarius",
            name = "Archer's Arrow",
            description = "Master all dignity facts for Sagittarius.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_capricorn",
            name = "Sea-Goat's Climb",
            description = "Master all dignity facts for Capricorn.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_aquarius",
            name = "Water Bearer",
            description = "Master all dignity facts for Aquarius.",
            category = "constellation"
        ),
        AchievementEntity(
            id = "constellation_pisces",
            name = "Twin Fish",
            description = "Master all dignity facts for Pisces.",
            category = "constellation"
        ),

        // --- Planet mastery cards: one per classical planet ---
        AchievementEntity(
            id = "planet_card_sun",
            name = "Solar Initiate",
            description = "Master all dignity cards related to the Sun.",
            category = "planet_card"
        ),
        AchievementEntity(
            id = "planet_card_moon",
            name = "Lunar Keeper",
            description = "Master all dignity cards related to the Moon.",
            category = "planet_card"
        ),
        AchievementEntity(
            id = "planet_card_mercury",
            name = "Hermetic Scholar",
            description = "Master all dignity cards related to Mercury.",
            category = "planet_card"
        ),
        AchievementEntity(
            id = "planet_card_venus",
            name = "Aphrodite's Favour",
            description = "Master all dignity cards related to Venus.",
            category = "planet_card"
        ),
        AchievementEntity(
            id = "planet_card_mars",
            name = "Martial Discipline",
            description = "Master all dignity cards related to Mars.",
            category = "planet_card"
        ),
        AchievementEntity(
            id = "planet_card_jupiter",
            name = "Jovial Wisdom",
            description = "Master all dignity cards related to Jupiter.",
            category = "planet_card"
        ),
        AchievementEntity(
            id = "planet_card_saturn",
            name = "Saturnine Patience",
            description = "Master all dignity cards related to Saturn.",
            category = "planet_card"
        ),

        // --- Milestone achievements ---
        AchievementEntity(
            id = "milestone_first_review",
            name = "First Light",
            description = "Complete your very first review session.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_10_cards",
            name = "Apprentice Astrologer",
            description = "Review 10 cards total.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_50_cards",
            name = "Student of the Heavens",
            description = "Review 50 cards total.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_100_cards",
            name = "Celestial Practitioner",
            description = "Review 100 cards total.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_500_cards",
            name = "Planetary Adept",
            description = "Review 500 cards total.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_1000_cards",
            name = "Master of Dignities",
            description = "Review 1,000 cards total.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_streak_3",
            name = "Three-Day Vigil",
            description = "Maintain a 3-day study streak.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_streak_7",
            name = "Week of Stars",
            description = "Maintain a 7-day study streak.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_streak_30",
            name = "Month of the Magi",
            description = "Maintain a 30-day study streak.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_all_domiciles",
            name = "House of Learning",
            description = "Answer all domicile cards correctly at least once.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_all_exaltations",
            name = "Elevated Understanding",
            description = "Answer all exaltation cards correctly at least once.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_all_triplicities",
            name = "Elemental Mastery",
            description = "Answer all triplicity cards correctly at least once.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_all_terms",
            name = "Bound by Knowledge",
            description = "Answer all term (bound) cards correctly at least once.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_all_decans",
            name = "Face of the Sky",
            description = "Answer all decan (face) cards correctly at least once.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_all_lots",
            name = "Fortune's Cartographer",
            description = "Answer all Hermetic lot cards correctly at least once.",
            category = "milestone"
        ),
        AchievementEntity(
            id = "milestone_complete_dignities",
            name = "Stellae Initiate",
            description = "Complete the full essential dignity curriculum.",
            category = "milestone"
        )
    )
}
