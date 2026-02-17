package com.stellae.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stellae.app.ui.components.PrimaryButton
import com.stellae.app.ui.components.SecondaryButton
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.components.StreakBadge
import com.stellae.app.ui.components.XpProgressBar
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Correct
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.Streak
import com.stellae.app.ui.theme.TextGold
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary
import kotlin.math.roundToInt

/**
 * Home screen — the app's landing destination.
 *
 * Usage:
 *   HomeScreen(
 *       onStartSession = { navController.navigate(Screen.Quiz.route) },
 *       onOpenWheel    = { navController.navigate(Screen.Wheel.route) },
 *   )
 */
@Composable
fun HomeScreen(
    onStartSession: () -> Unit,
    onOpenWheel: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val statusBarPadding    = WindowInsets.statusBars.asPaddingValues()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Box(modifier = Modifier
        .fillMaxSize()
        .background(BgDeep)
    ) {
        // Animated starfield fills the entire background.
        StarfieldBackground()

        // Scrollable content column with system-bar-aware padding.
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = statusBarPadding.calculateTopPadding())
                .padding(bottom = navigationBarPadding.calculateBottomPadding())
                .padding(horizontal = 20.dp),
        ) {

            Spacer(Modifier.height(32.dp))

            // ── App title ────────────────────────────────────────────────────
            Text(
                text      = "STELLAE",
                style     = StellaeTypography.displayLg.copy(
                    color        = Gold,
                    fontWeight   = FontWeight.Bold,
                    letterSpacing = 0.12.em,
                ),
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(8.dp))

            // ── Streak badge ─────────────────────────────────────────────────
            StreakBadge(count = uiState.streakCount)

            Spacer(Modifier.height(28.dp))

            // ── Rank + XP progress card ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(StellaeShapes.lg)
                    .background(BgCard)
                    .border(1.dp, BorderGlow, StellaeShapes.lg)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text(
                    text  = uiState.rankTitle,
                    style = StellaeTypography.displaySm.copy(color = TextPrimary),
                )

                Spacer(Modifier.height(10.dp))

                XpProgressBar(
                    progress = uiState.rankProgress,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text  = "${uiState.xpCurrent} / ${uiState.xpForNext} XP",
                    style = StellaeTypography.caption.copy(color = TextMuted),
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Daily mission card ────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(StellaeShapes.lg)
                    .background(BgElevated)
                    .border(1.dp, BorderSubtle, StellaeShapes.lg)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                Text(
                    text  = "TODAY'S MISSION",
                    style = StellaeTypography.label.copy(
                        color         = TextGold,
                        letterSpacing = 0.10.em,
                    ),
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text  = uiState.dailyMission,
                    style = StellaeTypography.bodyMd.copy(color = TextPrimary),
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Stats row ────────────────────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                StatCard(
                    label = "Due Reviews",
                    value = uiState.dueReviewCount.toString(),
                    valueColor = if (uiState.dueReviewCount > 0) Streak else TextPrimary,
                    modifier = Modifier.weight(1f),
                )

                StatCard(
                    label = "Accuracy",
                    value = "${(uiState.accuracy * 100).roundToInt()}%",
                    valueColor = when {
                        uiState.accuracy >= 0.8f -> Correct
                        uiState.accuracy > 0f    -> Streak
                        else                     -> TextMuted
                    },
                    modifier = Modifier.weight(1f),
                )

                StatCard(
                    label = "New Cards",
                    value = uiState.newCardCount.toString(),
                    valueColor = TextPrimary,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Primary CTA ──────────────────────────────────────────────────
            PrimaryButton(
                text      = "Start Session",
                onClick   = onStartSession,
                modifier  = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            // ── Secondary CTA ─────────────────────────────────────────────────
            SecondaryButton(
                text     = "Reference Wheel",
                onClick  = onOpenWheel,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── StatCard ──────────────────────────────────────────────────────────────────

/**
 * Small metric tile used in the 3-column stats row.
 */
@Composable
private fun StatCard(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(StellaeShapes.md)
            .background(BgCard)
            .border(1.dp, BorderSubtle, StellaeShapes.md)
            .padding(vertical = 14.dp, horizontal = 8.dp),
    ) {
        Text(
            text  = value,
            style = StellaeTypography.displaySm.copy(
                color      = valueColor,
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(2.dp))

        Text(
            text      = label,
            style     = StellaeTypography.caption.copy(color = TextSecondary),
            textAlign = TextAlign.Center,
        )
    }
}

// Suppress unresolved reference to `.em` — it is available via Compose UI's
// TextUnit extension, which is already on the classpath.
private val Double.em get() = androidx.compose.ui.unit.TextUnit(
    this.toFloat(),
    androidx.compose.ui.unit.TextUnitType.Em,
)
