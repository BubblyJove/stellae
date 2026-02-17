package com.stellae.app.ui.screens.summary

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stellae.app.domain.model.SessionResult
import com.stellae.app.ui.components.PrimaryButton
import com.stellae.app.ui.components.SecondaryButton
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.components.XpProgressBar
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.Streak
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary
import kotlin.math.roundToInt

/**
 * Session Summary screen — shown after the quiz session ends.
 *
 * Usage:
 *   SessionSummaryScreen(
 *       sessionResult        = result,
 *       onDone               = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Home.route) { inclusive = true } } },
 *       onPracticeWeakSpots  = { navController.navigate(Screen.Quiz.route) },
 *   )
 */
@Composable
fun SessionSummaryScreen(
    sessionResult: SessionResult,
    onDone: () -> Unit,
    onPracticeWeakSpots: () -> Unit,
    viewModel: SessionSummaryViewModel = hiltViewModel(),
) {
    // Push the result into the ViewModel on first composition.
    LaunchedEffect(sessionResult) {
        viewModel.provideResult(sessionResult)
    }

    val uiState by viewModel.uiState.collectAsState()

    val statusBarPadding     = WindowInsets.statusBars.asPaddingValues()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        StarfieldBackground()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = statusBarPadding.calculateTopPadding())
                .padding(bottom = navigationBarPadding.calculateBottomPadding())
                .padding(horizontal = 20.dp),
        ) {

            Spacer(Modifier.height(36.dp))

            // ── Star icon ────────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Gold.copy(alpha = 0.12f))
                    .border(2.dp, Gold.copy(alpha = 0.50f), CircleShape),
            ) {
                Text(
                    text  = "\u2B50",   // star emoji
                    style = StellaeTypography.displayMd.copy(color = Gold),
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── "Session Complete!" heading ───────────────────────────────────
            Text(
                text      = "Session Complete!",
                style     = StellaeTypography.displayMd.copy(
                    color      = Gold,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
            )

            // Rank-up banner (only if the user advanced a rank this session).
            if (uiState.didRankUp) {
                Spacer(Modifier.height(8.dp))

                Text(
                    text      = "Rank Up: ${uiState.newRankTitle}!",
                    style     = StellaeTypography.bodyMd.copy(
                        color      = Streak,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Stats card ───────────────────────────────────────────────────
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(StellaeShapes.lg)
                    .background(BgElevated)
                    .border(1.dp, BorderSubtle, StellaeShapes.lg)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                StatRow(label = "Cards Reviewed", value = uiState.cardsReviewed.toString())
                StatDivider()
                StatRow(
                    label = "Accuracy",
                    value = "${(uiState.accuracy * 100).roundToInt()}%",
                )
                StatDivider()
                StatRow(label = "XP Earned", value = "+${uiState.xpEarned} XP")
                StatDivider()
                StatRow(label = "Time", value = formatDuration(uiState.durationSeconds))
                StatDivider()
                StatRow(
                    label = "Streak",
                    value = "${uiState.streakCount} day${if (uiState.streakCount == 1) "" else "s"}",
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Rank progress ────────────────────────────────────────────────
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

            // ── Tip card ─────────────────────────────────────────────────────
            if (uiState.tip.isNotBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(StellaeShapes.lg)
                        .background(Gold.copy(alpha = 0.06f))
                        .border(1.dp, GoldDim.copy(alpha = 0.45f), StellaeShapes.lg)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Text(
                        text  = "TIP",
                        style = StellaeTypography.label.copy(
                            color         = Gold,
                            letterSpacing = 0.10.em,
                        ),
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text  = uiState.tip,
                        style = StellaeTypography.bodyMd.copy(color = TextSecondary),
                    )
                }

                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier.height(4.dp))

            // ── Done button ──────────────────────────────────────────────────
            PrimaryButton(
                text     = "Done",
                onClick  = onDone,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            // ── Practice Weak Spots button ────────────────────────────────────
            SecondaryButton(
                text     = "Practice Weak Spots",
                onClick  = onPracticeWeakSpots,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Small helper composables ──────────────────────────────────────────────────

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
    ) {
        Text(
            text  = label,
            style = StellaeTypography.bodyMd.copy(color = TextSecondary),
        )
        Text(
            text  = value,
            style = StellaeTypography.bodyMd.copy(
                color      = TextPrimary,
                fontWeight = FontWeight.SemiBold,
            ),
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BorderSubtle),
    )
}

// ── Utility ───────────────────────────────────────────────────────────────────

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
}

private val Double.em get() = androidx.compose.ui.unit.TextUnit(
    this.toFloat(),
    androidx.compose.ui.unit.TextUnitType.Em,
)
