package com.stellae.app.ui.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.components.XpProgressBar
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BgSurface
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Correct
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary
import com.stellae.app.ui.theme.Wrong

// ── Sample/stub data ──────────────────────────────────────────────────────────
// In production these would come from a ViewModel pulling from the database.

private data class TopicMastery(
    val label: String,
    val progress: Float,
    val color: Color
)

private data class ConfusedPair(
    val question: String,
    val wrong: String,
    val correct: String,
    val mistakeCount: Int
)

private data class SessionLog(
    val date: String,
    val cardCount: Int,
    val accuracy: Float,
    val xpEarned: Int
)

// ── ProgressDashboardScreen ───────────────────────────────────────────────────

/**
 * Analytics and mastery progress dashboard.
 *
 * Usage:
 *   ProgressDashboardScreen(onBack = { navController.popBackStack() })
 */
@Composable
fun ProgressDashboardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Stub data — replace with ViewModel state in a real implementation
    val overallAccuracy = 0.73f
    val weeklyXp = listOf(120, 85, 200, 55, 175, 90, 140)  // Mon..Sun
    val weeklyXpGoal = 700

    val topicMasteries = remember {
        listOf(
            TopicMastery("Domicile",   0.88f, Color(0xFFD4A832)),
            TopicMastery("Exaltation", 0.61f, Color(0xFFB8C4D8)),
            TopicMastery("Triplicity", 0.54f, Color(0xFFD45A30)),
            TopicMastery("Terms",      0.29f, Color(0xFF4CA77A)),
            TopicMastery("Decans",     0.41f, Color(0xFF4A6FA5))
        )
    }

    val confusedPairs = remember {
        listOf(
            ConfusedPair("Domicile of Jupiter?",   "Sagittarius only", "Sagittarius & Pisces", 7),
            ConfusedPair("Exaltation of Saturn?",  "Libra at 15°",     "Libra at 21°",         5),
            ConfusedPair("Night triplicity of Air?", "Saturn",          "Mercury",               4),
            ConfusedPair("Terms of 14° Aries?",    "Mars (12–20°)",    "Mercury (12–20°)",      3)
        )
    }

    val recentSessions = remember {
        listOf(
            SessionLog("Feb 17", 20, 0.85f, 95),
            SessionLog("Feb 16", 15, 0.73f, 62),
            SessionLog("Feb 15", 25, 0.92f, 145),
            SessionLog("Feb 14", 10, 0.60f, 38),
            SessionLog("Feb 13", 20, 0.80f, 88),
            SessionLog("Feb 12", 18, 0.78f, 79),
            SessionLog("Feb 11", 22, 0.95f, 160),
            SessionLog("Feb 10", 12, 0.67f, 44),
            SessionLog("Feb 9",  30, 0.70f, 110),
            SessionLog("Feb 8",  16, 0.56f, 30)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        StarfieldBackground(starCount = 60)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top bar
            item {
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
                        text = "Progress",
                        style = StellaeTypography.displaySm.copy(color = Gold)
                    )
                }
            }

            // Overall accuracy
            item {
                OverallAccuracyCard(
                    accuracy = overallAccuracy,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Weekly XP bar chart
            item {
                WeeklyXpCard(
                    dailyXp = weeklyXp,
                    goal = weeklyXpGoal,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Per-topic mastery
            item {
                TopicMasteryCard(
                    topics = topicMasteries,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Weakness areas
            item {
                WeaknessCard(
                    pairs = confusedPairs,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Recent sessions header
            item {
                Text(
                    text = "Recent Sessions",
                    style = StellaeTypography.bodyMd.copy(
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Session list
            items(recentSessions) { session ->
                SessionRow(
                    session = session,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

// ── Section cards ─────────────────────────────────────────────────────────────

@Composable
private fun OverallAccuracyCard(accuracy: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderGlow, StellaeShapes.lg)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Overall Accuracy",
                style = StellaeTypography.label.copy(color = TextMuted)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(accuracy * 100).toInt()}%",
                style = StellaeTypography.displayLg.copy(
                    color = when {
                        accuracy >= 0.90f -> Correct
                        accuracy >= 0.70f -> Gold
                        else -> Wrong
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 56.sp
                ),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            XpProgressBar(
                progress = accuracy,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when {
                    accuracy >= 0.90f -> "Excellent mastery"
                    accuracy >= 0.70f -> "Good progress — keep practicing"
                    else -> "Keep studying to improve"
                },
                style = StellaeTypography.bodySm.copy(color = TextSecondary),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeeklyXpCard(
    dailyXp: List<Int>,
    goal: Int,
    modifier: Modifier = Modifier
) {
    val dayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
    val maxXp = dailyXp.maxOrNull()?.coerceAtLeast(1) ?: 1
    val totalXp = dailyXp.sum()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderSubtle, StellaeShapes.lg)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Weekly XP",
                style = StellaeTypography.bodyMd.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "$totalXp / $goal XP",
                style = StellaeTypography.bodyMd.copy(color = Gold)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bar chart
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            dailyXp.forEachIndexed { idx, xp ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    val barFraction = xp.toFloat() / maxXp.toFloat()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(barFraction)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                if (idx == 6) Gold else GoldDim.copy(alpha = 0.6f)
                            )
                    )
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            dayLabels.forEach { day ->
                Text(
                    text = day,
                    style = StellaeTypography.caption.copy(color = TextMuted),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        XpProgressBar(
            progress = (totalXp.toFloat() / goal).coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Weekly goal: $goal XP",
            style = StellaeTypography.caption.copy(color = TextMuted),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun TopicMasteryCard(
    topics: List<TopicMastery>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderSubtle, StellaeShapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Topic Mastery",
            style = StellaeTypography.bodyMd.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        )
        topics.forEach { topic ->
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = topic.label,
                        style = StellaeTypography.bodyMd.copy(color = TextPrimary)
                    )
                    Text(
                        text = "${(topic.progress * 100).toInt()}%",
                        style = StellaeTypography.bodyMd.copy(
                            color = topic.color,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Custom colored progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(StellaeShapes.full)
                        .background(BgSurface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(topic.progress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(StellaeShapes.full)
                            .background(topic.color)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeaknessCard(
    pairs: List<ConfusedPair>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderSubtle, StellaeShapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Wrong,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Frequent Confusion",
                style = StellaeTypography.bodyMd.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        pairs.forEachIndexed { idx, pair ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(StellaeShapes.sm)
                    .background(BgElevated)
                    .padding(12.dp)
            ) {
                Text(
                    text = pair.question,
                    style = StellaeTypography.bodyMd.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = Wrong,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pair.wrong,
                        style = StellaeTypography.bodySm.copy(color = Wrong)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Correct,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = pair.correct,
                        style = StellaeTypography.bodySm.copy(color = Correct)
                    )
                }
                Text(
                    text = "${pair.mistakeCount}x confused",
                    style = StellaeTypography.caption.copy(color = TextMuted),
                    modifier = Modifier.align(Alignment.End)
                )
            }
            if (idx < pairs.size - 1) Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun SessionRow(session: SessionLog, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(StellaeShapes.md)
            .background(BgCard)
            .border(1.dp, BorderSubtle, StellaeShapes.md)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        // Date
        Text(
            text = session.date,
            style = StellaeTypography.bodyMd.copy(color = TextMuted),
            modifier = Modifier.width(56.dp)
        )

        // Cards count
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${session.cardCount} cards",
                style = StellaeTypography.bodyMd.copy(color = TextPrimary)
            )
        }

        // Accuracy
        val accuracyColor = when {
            session.accuracy >= 0.90f -> Correct
            session.accuracy >= 0.70f -> Gold
            else -> Wrong
        }
        Text(
            text = "${(session.accuracy * 100).toInt()}%",
            style = StellaeTypography.bodyMd.copy(
                color = accuracyColor,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.width(44.dp),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.width(12.dp))

        // XP
        Box(
            modifier = Modifier
                .clip(StellaeShapes.full)
                .background(Gold.copy(alpha = 0.12f))
                .border(1.dp, Gold.copy(alpha = 0.30f), StellaeShapes.full)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "+${session.xpEarned} XP",
                style = StellaeTypography.caption.copy(
                    color = Gold,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

private val BgSurface = Color(0xFF232A45)
