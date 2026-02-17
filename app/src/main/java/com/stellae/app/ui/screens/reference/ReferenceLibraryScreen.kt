package com.stellae.app.ui.screens.reference

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BgSurface
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary

// ── Astrological reference data ───────────────────────────────────────────────

private val SIGN_NAMES = arrayOf(
    "Aries", "Taurus", "Gemini", "Cancer",
    "Leo", "Virgo", "Libra", "Scorpio",
    "Sagittarius", "Capricorn", "Aquarius", "Pisces"
)
private val SIGN_GLYPHS = arrayOf(
    "\u2648", "\u2649", "\u264A", "\u264B",
    "\u264C", "\u264D", "\u264E", "\u264F",
    "\u2650", "\u2651", "\u2652", "\u2653"
)
private val PLANET_NAMES = arrayOf("", "Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter", "Saturn")
private val PLANET_GLYPHS = arrayOf("", "\u2609", "\u263D", "\u263F", "\u2640", "\u2642", "\u2643", "\u2644")
private val PLANET_COLORS = arrayOf(
    Color.Transparent,
    Color(0xFFD4A832), Color(0xFFB8C4D8), Color(0xFFC99A45),
    Color(0xFF4CA77A), Color(0xFFC44545), Color(0xFF4A6FA5), Color(0xFF7A8899)
)
private val DOMICILE_RULERS = arrayOf(5, 4, 3, 2, 1, 3, 4, 5, 6, 7, 7, 6)
private val EXALTATION_MAP = mapOf(
    0 to Triple(1, 19, "Aries"),
    1 to Triple(2, 3, "Taurus"),
    3 to Triple(6, 15, "Cancer"),
    5 to Triple(3, 15, "Virgo"),
    6 to Triple(7, 21, "Libra"),
    9 to Triple(5, 28, "Capricorn"),
    11 to Triple(4, 27, "Pisces")
)
private val TRIPLICITY_DATA = listOf(
    listOf("Fire", "Aries, Leo, Sagittarius", "Sun", "Jupiter", "Saturn"),
    listOf("Earth", "Taurus, Virgo, Capricorn", "Venus", "Moon", "Mars"),
    listOf("Air", "Gemini, Libra, Aquarius", "Saturn", "Mercury", "Jupiter"),
    listOf("Water", "Cancer, Scorpio, Pisces", "Venus", "Mars", "Moon")
)
private val TERMS_DATA = arrayOf(
    // sign -> list of (planet, startDeg, endDeg)
    arrayOf(Triple("Jupiter", 0, 6), Triple("Venus", 6, 12), Triple("Mercury", 12, 20), Triple("Mars", 20, 25), Triple("Saturn", 25, 30)),
    arrayOf(Triple("Venus", 0, 8), Triple("Mercury", 8, 14), Triple("Jupiter", 14, 22), Triple("Saturn", 22, 27), Triple("Mars", 27, 30)),
    arrayOf(Triple("Mercury", 0, 6), Triple("Jupiter", 6, 12), Triple("Venus", 12, 17), Triple("Mars", 17, 24), Triple("Saturn", 24, 30)),
    arrayOf(Triple("Mars", 0, 7), Triple("Venus", 7, 13), Triple("Mercury", 13, 19), Triple("Jupiter", 19, 26), Triple("Saturn", 26, 30)),
    arrayOf(Triple("Jupiter", 0, 6), Triple("Venus", 6, 11), Triple("Saturn", 11, 18), Triple("Mercury", 18, 24), Triple("Mars", 24, 30)),
    arrayOf(Triple("Mercury", 0, 7), Triple("Venus", 7, 17), Triple("Jupiter", 17, 21), Triple("Mars", 21, 28), Triple("Saturn", 28, 30)),
    arrayOf(Triple("Saturn", 0, 6), Triple("Venus", 6, 14), Triple("Jupiter", 14, 21), Triple("Mercury", 21, 28), Triple("Mars", 28, 30)),
    arrayOf(Triple("Mars", 0, 7), Triple("Venus", 7, 11), Triple("Mercury", 11, 19), Triple("Jupiter", 19, 24), Triple("Saturn", 24, 30)),
    arrayOf(Triple("Jupiter", 0, 12), Triple("Venus", 12, 17), Triple("Mercury", 17, 21), Triple("Saturn", 21, 26), Triple("Mars", 26, 30)),
    arrayOf(Triple("Venus", 0, 7), Triple("Jupiter", 7, 14), Triple("Mercury", 14, 22), Triple("Saturn", 22, 26), Triple("Mars", 26, 30)),
    arrayOf(Triple("Saturn", 0, 7), Triple("Mercury", 7, 13), Triple("Venus", 13, 20), Triple("Jupiter", 20, 25), Triple("Mars", 25, 30)),
    arrayOf(Triple("Venus", 0, 12), Triple("Jupiter", 12, 16), Triple("Mercury", 16, 19), Triple("Mars", 19, 28), Triple("Saturn", 28, 30))
)
private val DECAN_RULERS = arrayOf(
    arrayOf("Mars", "Sun", "Venus"),
    arrayOf("Mercury", "Moon", "Saturn"),
    arrayOf("Jupiter", "Mars", "Sun"),
    arrayOf("Venus", "Mercury", "Moon"),
    arrayOf("Saturn", "Jupiter", "Mars"),
    arrayOf("Sun", "Venus", "Mercury"),
    arrayOf("Moon", "Saturn", "Jupiter"),
    arrayOf("Mars", "Sun", "Venus"),
    arrayOf("Mercury", "Moon", "Saturn"),
    arrayOf("Jupiter", "Mars", "Sun"),
    arrayOf("Venus", "Mercury", "Moon"),
    arrayOf("Saturn", "Jupiter", "Mars")
)
private val LOTS_DATA = listOf(
    listOf("Lot of Fortune", "Asc + Moon - Sun (day) / Asc + Sun - Moon (night)"),
    listOf("Lot of Spirit", "Asc + Sun - Moon (day) / Asc + Moon - Sun (night)"),
    listOf("Lot of Eros", "Asc + Venus - Spirit"),
    listOf("Lot of Necessity", "Asc + Fortune - Venus"),
    listOf("Lot of Courage", "Asc + Fortune - Mars"),
    listOf("Lot of Victory", "Asc + Jupiter - Spirit"),
    listOf("Lot of Nemesis", "Asc + Fortune - Saturn")
)

// ── ReferenceLibraryScreen ────────────────────────────────────────────────────

/**
 * Searchable encyclopedia of all essential dignity data, organized into
 * collapsible sections.
 *
 * Usage:
 *   ReferenceLibraryScreen(onBack = { navController.popBackStack() })
 */
@Composable
fun ReferenceLibraryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by rememberSaveable { mutableStateOf("") }
    val lowerQuery = query.lowercase()

    // Section expanded states
    var domicileExpanded    by rememberSaveable { mutableStateOf(true) }
    var exaltationExpanded  by rememberSaveable { mutableStateOf(false) }
    var triplicityExpanded  by rememberSaveable { mutableStateOf(false) }
    var termsExpanded       by rememberSaveable { mutableStateOf(false) }
    var decansExpanded      by rememberSaveable { mutableStateOf(false) }
    var lotsExpanded        by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        StarfieldBackground(starCount = 60)

        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Gold
                    )
                }
                Text(
                    text = "Reference Library",
                    style = StellaeTypography.displaySm.copy(color = Gold),
                    modifier = Modifier.weight(1f)
                )
            }

            // Search bar
            SearchBar(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // ── Domiciles ─────────────────────────────────────────────────
                item {
                    SectionHeader(
                        title = "Domiciles",
                        subtitle = "Each planet's home signs",
                        accentColor = Color(0xFFD4A832),
                        expanded = domicileExpanded,
                        onToggle = { domicileExpanded = !domicileExpanded }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = domicileExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(StellaeShapes.lg)
                                .background(BgCard)
                                .border(1.dp, BorderSubtle, StellaeShapes.lg)
                        ) {
                            SIGN_NAMES.forEachIndexed { i, signName ->
                                val pid = DOMICILE_RULERS[i]
                                if (lowerQuery.isEmpty() ||
                                    signName.lowercase().contains(lowerQuery) ||
                                    PLANET_NAMES[pid].lowercase().contains(lowerQuery)
                                ) {
                                    DomicileRow(
                                        signGlyph = SIGN_GLYPHS[i],
                                        signName = signName,
                                        planetGlyph = PLANET_GLYPHS[pid],
                                        planetName = PLANET_NAMES[pid],
                                        planetColor = PLANET_COLORS[pid]
                                    )
                                    if (i < 11) HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // ── Exaltations ───────────────────────────────────────────────
                item {
                    SectionHeader(
                        title = "Exaltations",
                        subtitle = "Planets at their highest dignity",
                        accentColor = Color(0xFFB8C4D8),
                        expanded = exaltationExpanded,
                        onToggle = { exaltationExpanded = !exaltationExpanded }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = exaltationExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(StellaeShapes.lg)
                                .background(BgCard)
                                .border(1.dp, BorderSubtle, StellaeShapes.lg)
                        ) {
                            EXALTATION_MAP.entries.forEachIndexed { idx, (signIdx, data) ->
                                val (planetId, degree, signName) = data
                                if (lowerQuery.isEmpty() ||
                                    signName.lowercase().contains(lowerQuery) ||
                                    PLANET_NAMES[planetId].lowercase().contains(lowerQuery)
                                ) {
                                    ExaltationRow(
                                        planetGlyph = PLANET_GLYPHS[planetId],
                                        planetName = PLANET_NAMES[planetId],
                                        planetColor = PLANET_COLORS[planetId],
                                        signGlyph = SIGN_GLYPHS[signIdx],
                                        signName = signName,
                                        degree = degree
                                    )
                                    if (idx < EXALTATION_MAP.size - 1) {
                                        HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Triplicities ──────────────────────────────────────────────
                item {
                    SectionHeader(
                        title = "Triplicities",
                        subtitle = "Elemental rulers by day and night",
                        accentColor = Color(0xFFD45A30),
                        expanded = triplicityExpanded,
                        onToggle = { triplicityExpanded = !triplicityExpanded }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = triplicityExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(StellaeShapes.lg)
                                .background(BgCard)
                                .border(1.dp, BorderSubtle, StellaeShapes.lg)
                        ) {
                            TRIPLICITY_DATA.forEachIndexed { idx, row ->
                                val element = row[0]
                                val signs = row[1]
                                val day = row[2]
                                val night = row[3]
                                val part = row[4]
                                if (lowerQuery.isEmpty() ||
                                    element.lowercase().contains(lowerQuery) ||
                                    signs.lowercase().contains(lowerQuery) ||
                                    day.lowercase().contains(lowerQuery) ||
                                    night.lowercase().contains(lowerQuery)
                                ) {
                                    TriplicityRow(element, signs, day, night, part)
                                    if (idx < TRIPLICITY_DATA.size - 1) {
                                        HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Terms ─────────────────────────────────────────────────────
                item {
                    SectionHeader(
                        title = "Terms (Egyptian)",
                        subtitle = "Degree-based rulership within each sign",
                        accentColor = Color(0xFF4CA77A),
                        expanded = termsExpanded,
                        onToggle = { termsExpanded = !termsExpanded }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = termsExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(StellaeShapes.lg)
                                .background(BgCard)
                                .border(1.dp, BorderSubtle, StellaeShapes.lg)
                        ) {
                            SIGN_NAMES.forEachIndexed { signIdx, signName ->
                                if (lowerQuery.isEmpty() || signName.lowercase().contains(lowerQuery)) {
                                    TermsSignSection(
                                        signGlyph = SIGN_GLYPHS[signIdx],
                                        signName = signName,
                                        terms = TERMS_DATA[signIdx]
                                    )
                                    if (signIdx < 11) HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // ── Decans ────────────────────────────────────────────────────
                item {
                    SectionHeader(
                        title = "Decans (Ptolemaic)",
                        subtitle = "Three 10-degree faces per sign",
                        accentColor = Color(0xFF4A6FA5),
                        expanded = decansExpanded,
                        onToggle = { decansExpanded = !decansExpanded }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = decansExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(StellaeShapes.lg)
                                .background(BgCard)
                                .border(1.dp, BorderSubtle, StellaeShapes.lg)
                        ) {
                            SIGN_NAMES.forEachIndexed { signIdx, signName ->
                                if (lowerQuery.isEmpty() || signName.lowercase().contains(lowerQuery)) {
                                    DecanSignSection(
                                        signGlyph = SIGN_GLYPHS[signIdx],
                                        signName = signName,
                                        decans = DECAN_RULERS[signIdx]
                                    )
                                    if (signIdx < 11) HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }

                // ── Lots ──────────────────────────────────────────────────────
                item {
                    SectionHeader(
                        title = "Arabic Lots",
                        subtitle = "Computed sensitive points",
                        accentColor = Color(0xFF9B59B6),
                        expanded = lotsExpanded,
                        onToggle = { lotsExpanded = !lotsExpanded }
                    )
                }
                item {
                    AnimatedVisibility(
                        visible = lotsExpanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(StellaeShapes.lg)
                                .background(BgCard)
                                .border(1.dp, BorderSubtle, StellaeShapes.lg)
                        ) {
                            LOTS_DATA.forEachIndexed { idx, lot ->
                                val name = lot[0]
                                val formula = lot[1]
                                if (lowerQuery.isEmpty() ||
                                    name.lowercase().contains(lowerQuery) ||
                                    formula.lowercase().contains(lowerQuery)
                                ) {
                                    LotRow(name = name, formula = formula)
                                    if (idx < LOTS_DATA.size - 1) {
                                        HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

// ── Sub-components ────────────────────────────────────────────────────────────

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(StellaeShapes.md)
            .background(BgElevated)
            .border(1.dp, if (query.isNotEmpty()) GoldDim else BorderSubtle, StellaeShapes.md)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = if (query.isNotEmpty()) Gold else TextMuted,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = "Search planets, signs, dignities...",
                        style = StellaeTypography.bodyMd.copy(color = TextMuted)
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    cursorBrush = SolidColor(Gold),
                    textStyle = StellaeTypography.bodyMd.copy(color = TextPrimary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (query.isNotEmpty()) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    tint = TextMuted,
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { onQueryChange("") }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String,
    accentColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(StellaeShapes.md)
            .background(BgSurface)
            .border(1.dp, accentColor.copy(alpha = 0.30f), StellaeShapes.md)
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = StellaeTypography.bodyMd.copy(
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            )
            Text(
                text = subtitle,
                style = StellaeTypography.bodySm.copy(color = TextSecondary)
            )
        }
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (expanded) "Collapse" else "Expand",
            tint = accentColor
        )
    }
}

@Composable
private fun DomicileRow(
    signGlyph: String,
    signName: String,
    planetGlyph: String,
    planetName: String,
    planetColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = signGlyph,
            style = StellaeTypography.displaySm.copy(color = TextPrimary),
            modifier = Modifier.width(36.dp)
        )
        Text(
            text = signName,
            style = StellaeTypography.bodyMd.copy(color = TextPrimary),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$planetGlyph $planetName",
            style = StellaeTypography.bodyMd.copy(
                color = planetColor,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun ExaltationRow(
    planetGlyph: String,
    planetName: String,
    planetColor: Color,
    signGlyph: String,
    signName: String,
    degree: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = planetGlyph,
            style = StellaeTypography.displaySm.copy(color = planetColor),
            modifier = Modifier.width(32.dp)
        )
        Text(
            text = planetName,
            style = StellaeTypography.bodyMd.copy(
                color = planetColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "$signGlyph $signName $degree°",
            style = StellaeTypography.bodyMd.copy(color = TextSecondary)
        )
    }
}

@Composable
private fun TriplicityRow(
    element: String,
    signs: String,
    day: String,
    night: String,
    participating: String
) {
    val elementColor = when (element) {
        "Fire"  -> Color(0xFFD45A30)
        "Earth" -> Color(0xFF6B8C42)
        "Air"   -> Color(0xFF5A8EC9)
        "Water" -> Color(0xFF3A7AAA)
        else    -> Gold
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(StellaeShapes.sm)
                    .background(elementColor.copy(alpha = 0.18f))
                    .border(1.dp, elementColor.copy(alpha = 0.40f), StellaeShapes.sm)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = element,
                    style = StellaeTypography.label.copy(color = elementColor)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = signs,
                style = StellaeTypography.bodySm.copy(color = TextSecondary)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            RulerChip(label = "Day", value = day)
            RulerChip(label = "Night", value = night)
            RulerChip(label = "Part.", value = participating)
        }
    }
}

@Composable
private fun RulerChip(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = StellaeTypography.caption.copy(color = TextMuted)
        )
        Text(
            text = value,
            style = StellaeTypography.bodySm.copy(
                color = Gold,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun TermsSignSection(
    signGlyph: String,
    signName: String,
    terms: Array<Triple<String, Int, Int>>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = signGlyph,
                style = StellaeTypography.bodyLg.copy(color = TextPrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = signName,
                style = StellaeTypography.bodyMd.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            terms.forEach { (planet, start, end) ->
                Box(
                    modifier = Modifier
                        .weight((end - start).toFloat())
                        .clip(StellaeShapes.sm)
                        .background(BgSurface)
                        .border(1.dp, BorderSubtle, StellaeShapes.sm)
                        .padding(horizontal = 4.dp, vertical = 4.dp)
                ) {
                    Column {
                        Text(
                            text = planet.take(3),
                            style = StellaeTypography.caption.copy(
                                color = Gold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "$start-$end",
                            style = StellaeTypography.caption.copy(color = TextMuted)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DecanSignSection(
    signGlyph: String,
    signName: String,
    decans: Array<String>
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = signGlyph,
            style = StellaeTypography.bodyLg.copy(color = TextPrimary),
            modifier = Modifier.width(28.dp)
        )
        Text(
            text = signName,
            style = StellaeTypography.bodyMd.copy(color = TextPrimary),
            modifier = Modifier.width(100.dp)
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            decans.forEachIndexed { idx, planet ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${idx * 10 + 1}-${(idx + 1) * 10}°",
                        style = StellaeTypography.caption.copy(color = TextMuted)
                    )
                    Text(
                        text = planet.take(3),
                        style = StellaeTypography.bodySm.copy(color = Gold)
                    )
                }
            }
        }
    }
}

@Composable
private fun LotRow(name: String, formula: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = name,
            style = StellaeTypography.bodyMd.copy(
                color = Gold,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = formula,
            style = StellaeTypography.bodySm.copy(color = TextSecondary)
        )
    }
}
