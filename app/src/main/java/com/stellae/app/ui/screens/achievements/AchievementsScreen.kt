package com.stellae.app.ui.screens.achievements

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellae.app.domain.gamification.AchievementSystem
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.components.XpProgressBar
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BgSurface
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldBright
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary

// ── Stub progress data ────────────────────────────────────────────────────────
// In production these would come from a ViewModel using AchievementDao.

private data class AchievementProgress(
    val id: String,
    val starsEarned: Int,
    val isUnlocked: Boolean
)

private val CONSTELLATION_PROGRESS = mapOf(
    "aries_constellation"       to AchievementProgress("aries_constellation",       5, true),
    "taurus_constellation"      to AchievementProgress("taurus_constellation",      5, true),
    "gemini_constellation"      to AchievementProgress("gemini_constellation",      3, false),
    "cancer_constellation"      to AchievementProgress("cancer_constellation",      2, false),
    "leo_constellation"         to AchievementProgress("leo_constellation",         5, true),
    "virgo_constellation"       to AchievementProgress("virgo_constellation",       1, false),
    "libra_constellation"       to AchievementProgress("libra_constellation",       0, false),
    "scorpio_constellation"     to AchievementProgress("scorpio_constellation",     4, false),
    "sagittarius_constellation" to AchievementProgress("sagittarius_constellation", 0, false),
    "capricorn_constellation"   to AchievementProgress("capricorn_constellation",   0, false),
    "aquarius_constellation"    to AchievementProgress("aquarius_constellation",    2, false),
    "pisces_constellation"      to AchievementProgress("pisces_constellation",      0, false)
)

private val MILESTONE_PROGRESS = mapOf(
    "first_session"   to AchievementProgress("first_session",   1, true),
    "week_streak"     to AchievementProgress("week_streak",     1, true),
    "month_streak"    to AchievementProgress("month_streak",    0, false),
    "speed_demon"     to AchievementProgress("speed_demon",     4, false),
    "perfect_session" to AchievementProgress("perfect_session", 1, true),
    "all_domiciles"   to AchievementProgress("all_domiciles",   1, true),
    "all_exaltations" to AchievementProgress("all_exaltations", 0, false),
    "lot_master"      to AchievementProgress("lot_master",      2, false)
)

// Zodiac glyph map by constellation ID prefix
private val SIGN_GLYPHS = mapOf(
    "aries"       to "\u2648",
    "taurus"      to "\u2649",
    "gemini"      to "\u264A",
    "cancer"      to "\u264B",
    "leo"         to "\u264C",
    "virgo"       to "\u264D",
    "libra"       to "\u264E",
    "scorpio"     to "\u264F",
    "sagittarius" to "\u2650",
    "capricorn"   to "\u2651",
    "aquarius"    to "\u2652",
    "pisces"      to "\u2653"
)

// ── AchievementsScreen ────────────────────────────────────────────────────────

/**
 * Collection view showing constellation badges and milestone achievements.
 *
 * Usage:
 *   AchievementsScreen(onBack = { navController.popBackStack() })
 */
@Composable
fun AchievementsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Constellations", "Milestones")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        StarfieldBackground(starCount = 80)

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
                    text = "Achievements",
                    style = StellaeTypography.displaySm.copy(color = Gold),
                    modifier = Modifier.weight(1f)
                )

                // Unlocked count badge
                val unlockedCount = (CONSTELLATION_PROGRESS.values.count { it.isUnlocked } +
                        MILESTONE_PROGRESS.values.count { it.isUnlocked })
                val totalCount = CONSTELLATION_PROGRESS.size + MILESTONE_PROGRESS.size
                Box(
                    modifier = Modifier
                        .clip(StellaeShapes.full)
                        .background(Gold.copy(alpha = 0.12f))
                        .border(1.dp, GoldDim.copy(alpha = 0.40f), StellaeShapes.full)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$unlockedCount / $totalCount",
                        style = StellaeTypography.caption.copy(
                            color = Gold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = BgCard,
                contentColor = Gold,
                edgePadding = 0.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = Gold
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = {
                            Text(
                                text = title,
                                style = StellaeTypography.bodyMd.copy(
                                    color = if (selectedTab == idx) Gold else TextSecondary,
                                    fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    )
                }
            }

            // Tab content
            when (selectedTab) {
                0 -> ConstellationsTab()
                1 -> MilestonesTab()
            }
        }
    }
}

// ── Constellations Tab ────────────────────────────────────────────────────────

@Composable
private fun ConstellationsTab() {
    val constellations = AchievementSystem.constellations

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(constellations) { def ->
            val signKey = def.id.removeSuffix("_constellation")
            val glyph = SIGN_GLYPHS[signKey] ?: "?"
            val progress = CONSTELLATION_PROGRESS[def.id]
            val isUnlocked = progress?.isUnlocked == true
            val starsEarned = progress?.starsEarned ?: 0

            ConstellationBadge(
                glyph = glyph,
                name = def.name,
                starsEarned = starsEarned,
                starsTotal = def.starsTotal,
                isUnlocked = isUnlocked
            )
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun ConstellationBadge(
    glyph: String,
    name: String,
    starsEarned: Int,
    starsTotal: Int,
    isUnlocked: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = if (isUnlocked) 0.3f else 0f,
        targetValue = if (isUnlocked) 0.7f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val borderColor = when {
        isUnlocked     -> Gold
        starsEarned > 0 -> GoldDim.copy(alpha = 0.5f)
        else           -> BorderSubtle
    }
    val bgColor = when {
        isUnlocked      -> Gold.copy(alpha = 0.12f)
        starsEarned > 0 -> BgElevated
        else            -> BgCard.copy(alpha = 0.5f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(StellaeShapes.lg)
            .background(bgColor)
            .then(
                if (isUnlocked) {
                    Modifier
                        .graphicsLayer { alpha = 0.99f }
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Gold.copy(alpha = glowAlpha * 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                        }
                } else Modifier
            )
            .border(1.dp, borderColor, StellaeShapes.lg)
            .padding(12.dp)
    ) {
        // Glyph circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked)
                        Brush.radialGradient(
                            colors = listOf(Gold.copy(alpha = 0.25f), Color.Transparent)
                        )
                    else
                        Brush.radialGradient(
                            colors = listOf(BgSurface, BgSurface)
                        )
                )
                .border(
                    width = if (isUnlocked) 2.dp else 1.dp,
                    color = if (isUnlocked) Gold else BorderSubtle,
                    shape = CircleShape
                )
        ) {
            Text(
                text = glyph,
                style = StellaeTypography.displaySm.copy(
                    color = if (isUnlocked) Gold else TextMuted,
                    fontSize = 24.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            style = StellaeTypography.caption.copy(
                color = if (isUnlocked) TextPrimary else TextMuted,
                fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal,
                lineHeight = 15.sp
            ),
            textAlign = TextAlign.Center,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Progress stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.padding(top = 2.dp)
        ) {
            repeat(starsTotal) { idx ->
                val isFilled = idx < starsEarned
                Text(
                    text = if (isFilled) "\u2605" else "\u2606",
                    style = StellaeTypography.caption.copy(
                        color = if (isFilled) Gold else TextMuted.copy(alpha = 0.5f),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

// ── Milestones Tab ────────────────────────────────────────────────────────────

@Composable
private fun MilestonesTab() {
    val milestones = AchievementSystem.milestones

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(milestones) { def ->
            val progress = MILESTONE_PROGRESS[def.id]
            val isUnlocked = progress?.isUnlocked == true
            val starsEarned = progress?.starsEarned ?: 0

            MilestoneTile(
                name = def.name,
                description = def.description,
                starsEarned = starsEarned,
                starsTotal = def.starsTotal,
                isUnlocked = isUnlocked
            )
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun MilestoneTile(
    name: String,
    description: String,
    starsEarned: Int,
    starsTotal: Int,
    isUnlocked: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "milestoneGlow")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = if (isUnlocked) 0.4f else 0f,
        targetValue = if (isUnlocked) 1.0f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(if (isUnlocked) Gold.copy(alpha = 0.08f) else BgCard)
            .border(
                1.dp,
                if (isUnlocked) Gold.copy(alpha = 0.40f + shimmer * 0.20f) else BorderSubtle,
                StellaeShapes.lg
            )
            .padding(14.dp)
    ) {
        // Badge icon
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isUnlocked)
                        Brush.radialGradient(
                            colors = listOf(Gold.copy(alpha = 0.30f), Color.Transparent)
                        )
                    else
                        Brush.radialGradient(
                            colors = listOf(BgElevated, BgElevated)
                        )
                )
                .border(
                    width = if (isUnlocked) 2.dp else 1.dp,
                    color = if (isUnlocked) Gold else BorderSubtle,
                    shape = CircleShape
                )
        ) {
            Text(
                text = if (isUnlocked) "\u2605" else "\u2606",
                style = StellaeTypography.bodyLg.copy(
                    color = if (isUnlocked) GoldBright else TextMuted,
                    fontSize = 22.sp
                )
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = StellaeTypography.bodyMd.copy(
                    color = if (isUnlocked) Gold else TextSecondary,
                    fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal
                )
            )
            Text(
                text = description,
                style = StellaeTypography.bodySm.copy(color = TextMuted),
                modifier = Modifier.padding(top = 2.dp)
            )

            // Progress bar (for multi-star milestones)
            if (starsTotal > 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    XpProgressBar(
                        progress = starsEarned.toFloat() / starsTotal.toFloat(),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "$starsEarned / $starsTotal",
                        style = StellaeTypography.caption.copy(
                            color = if (isUnlocked) Gold else TextMuted
                        )
                    )
                }
            }
        }

        // Single-star status indicator
        if (starsTotal == 1) {
            Box(
                modifier = Modifier
                    .clip(StellaeShapes.full)
                    .background(
                        if (isUnlocked) Gold.copy(alpha = 0.15f) else BgElevated
                    )
                    .border(
                        1.dp,
                        if (isUnlocked) Gold.copy(alpha = 0.50f) else BorderSubtle,
                        StellaeShapes.full
                    )
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isUnlocked) "Earned" else "Locked",
                    style = StellaeTypography.caption.copy(
                        color = if (isUnlocked) Gold else TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

private val BgSurface = Color(0xFF232A45)
