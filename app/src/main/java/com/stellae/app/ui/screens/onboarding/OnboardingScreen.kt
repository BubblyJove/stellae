package com.stellae.app.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellae.app.ui.components.PrimaryButton
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

// ── Onboarding page data ──────────────────────────────────────────────────────

private data class HowItWorksStep(
    val icon: String,
    val title: String,
    val description: String
)

private val HOW_IT_WORKS_STEPS = listOf(
    HowItWorksStep(
        icon = "\u2728",
        title = "Learn",
        description = "Study the seven planets, twelve signs, and five essential dignity systems through illustrated reference cards."
    ),
    HowItWorksStep(
        icon = "\u2642",
        title = "Practice",
        description = "Reinforce your knowledge with adaptive flashcard quizzes. Our spaced-repetition engine targets your weak points."
    ),
    HowItWorksStep(
        icon = "\u2609",
        title = "Master",
        description = "Achieve precision through speed drills, boss challenges, and lot calculations until essential dignity becomes instinct."
    )
)

// ── OnboardingScreen ──────────────────────────────────────────────────────────

/**
 * Three-page onboarding flow for new Stellae users.
 *
 * Page 1: Welcome — animated star/title screen.
 * Page 2: How It Works — learn/practice/master feature overview.
 * Page 3: Set Your Goal — weekly commitment selector.
 *
 * Usage:
 *   OnboardingScreen(onComplete = { navController.navigate(Screen.Home.route) })
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    var weeklyGoal by rememberSaveable { mutableIntStateOf(5) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        StarfieldBackground(starCount = 120)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Pager fills available space above bottom controls
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> WelcomePage()
                    1 -> HowItWorksPage()
                    2 -> SetGoalPage(
                        weeklyGoal = weeklyGoal,
                        onGoalChange = { weeklyGoal = it }
                    )
                }
            }

            // Bottom controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 24.dp)
            ) {
                // Dot indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    repeat(3) { idx ->
                        val isActive = pagerState.currentPage == idx
                        Box(
                            modifier = Modifier
                                .then(
                                    if (isActive) Modifier.width(24.dp) else Modifier.size(8.dp)
                                )
                                .height(8.dp)
                                .clip(StellaeShapes.full)
                                .background(if (isActive) Gold else GoldDim.copy(alpha = 0.40f))
                        )
                    }
                }

                // CTA button
                val isLastPage = pagerState.currentPage == 2
                PrimaryButton(
                    text = if (isLastPage) "Begin Journey" else "Continue",
                    onClick = {
                        if (isLastPage) {
                            onComplete()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Skip on pages 0 and 1
                AnimatedVisibility(
                    visible = !isLastPage,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "Skip",
                        style = StellaeTypography.bodyMd.copy(color = TextMuted),
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clickable(onClick = onComplete),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// ── Page 1: Welcome ───────────────────────────────────────────────────────────

@Composable
private fun WelcomePage() {
    val infiniteTransition = rememberInfiniteTransition(label = "starPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val innerPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "inner"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        // Animated star emblem
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(160.dp)
        ) {
            // Outer glow ring
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Gold.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Mid ring
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(innerPulse)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Gold.copy(alpha = 0.25f),
                                Color.Transparent
                            )
                        )
                    )
            )
            // Inner circle with glyph
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(BgElevated)
                    .border(2.dp, Gold, CircleShape)
            ) {
                Text(
                    text = "\u2609", // Sun glyph
                    style = StellaeTypography.displayLg.copy(
                        color = Gold,
                        fontSize = 40.sp
                    )
                )
            }
            // Decorative stars
            StarDecorations()
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Welcome to",
            style = StellaeTypography.bodyLg.copy(color = TextSecondary),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Stellae",
            style = StellaeTypography.displayLg.copy(
                color = Gold,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Master the ancient art of essential dignity — the cornerstone of classical astrology.",
            style = StellaeTypography.bodyLg.copy(
                color = TextSecondary,
                lineHeight = 28.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Feature pill badges
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf("Domiciles", "Exaltations", "Triplicities", "Terms", "Decans").forEach { tag ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(StellaeShapes.full)
                        .background(Gold.copy(alpha = 0.10f))
                        .border(1.dp, GoldDim.copy(alpha = 0.40f), StellaeShapes.full)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tag,
                        style = StellaeTypography.caption.copy(color = Gold)
                    )
                }
            }
        }
    }
}

@Composable
private fun StarDecorations() {
    // Small decorative star glyphs placed around the central emblem
    val stars = remember {
        listOf(
            Triple("\u2605", (-60).dp, (-50).dp),
            Triple("\u2605", 55.dp, (-45).dp),
            Triple("\u2606", (-45).dp, 55.dp),
            Triple("\u2605", 50.dp, 52.dp),
            Triple("\u2606", (-65).dp, 5.dp),
            Triple("\u2605", 62.dp, 8.dp)
        )
    }
    Box(modifier = Modifier.size(160.dp)) {
        stars.forEach { (glyph, xOff, yOff) ->
            Text(
                text = glyph,
                style = StellaeTypography.bodySm.copy(
                    color = Gold.copy(alpha = 0.6f),
                    fontSize = 12.sp
                ),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = (xOff.value + 80).dp, top = (yOff.value + 80).dp)
            )
        }
    }
}

// ── Page 2: How It Works ──────────────────────────────────────────────────────

@Composable
private fun HowItWorksPage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
    ) {
        Text(
            text = "How It Works",
            style = StellaeTypography.displayMd.copy(color = Gold),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Three simple stages to deep mastery",
            style = StellaeTypography.bodyMd.copy(color = TextSecondary),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 36.dp)
        )

        HOW_IT_WORKS_STEPS.forEachIndexed { idx, step ->
            HowItWorksCard(step = step, stepNumber = idx + 1)
            if (idx < HOW_IT_WORKS_STEPS.size - 1) {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun HowItWorksCard(step: HowItWorksStep, stepNumber: Int) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderGlow, StellaeShapes.lg)
            .padding(16.dp)
    ) {
        // Icon circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Gold.copy(alpha = 0.12f))
                .border(1.dp, Gold.copy(alpha = 0.40f), CircleShape)
        ) {
            Text(
                text = step.icon,
                style = StellaeTypography.displaySm.copy(fontSize = 22.sp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(Gold)
                ) {
                    Text(
                        text = stepNumber.toString(),
                        style = StellaeTypography.caption.copy(
                            color = BgDeep,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = step.title,
                    style = StellaeTypography.bodyMd.copy(
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = step.description,
                style = StellaeTypography.bodySm.copy(
                    color = TextSecondary,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

// ── Page 3: Set Your Goal ─────────────────────────────────────────────────────

@Composable
private fun SetGoalPage(
    weeklyGoal: Int,
    onGoalChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
    ) {
        // Constellation motif
        Text(
            text = "\u2648\u2649\u264A\u264B\u264C\u264D",
            style = StellaeTypography.displayMd.copy(
                color = Gold.copy(alpha = 0.50f),
                fontSize = 28.sp
            ),
            letterSpacing = 4.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Set Your Goal",
            style = StellaeTypography.displayMd.copy(color = Gold),
            textAlign = TextAlign.Center
        )
        Text(
            text = "How many days per week will you commit to studying?",
            style = StellaeTypography.bodyLg.copy(
                color = TextSecondary,
                lineHeight = 26.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp, bottom = 40.dp)
        )

        // Goal selector cards
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            listOf(
                Triple(3, "Casual", "3 days"),
                Triple(5, "Committed", "5 days"),
                Triple(7, "Dedicated", "7 days")
            ).forEach { (goal, label, days) ->
                val isSelected = goal == weeklyGoal
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(StellaeShapes.lg)
                        .background(
                            if (isSelected) Gold.copy(alpha = 0.15f) else BgCard
                        )
                        .border(
                            2.dp,
                            if (isSelected) Gold else BorderSubtle,
                            StellaeShapes.lg
                        )
                        .clickable { onGoalChange(goal) }
                        .padding(vertical = 20.dp, horizontal = 8.dp)
                ) {
                    Text(
                        text = goal.toString(),
                        style = StellaeTypography.displayMd.copy(
                            color = if (isSelected) Gold else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 36.sp
                        )
                    )
                    Text(
                        text = "days",
                        style = StellaeTypography.bodySm.copy(color = TextMuted)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = label,
                        style = StellaeTypography.bodySm.copy(
                            color = if (isSelected) Gold else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Motivational text
        val motivationText = when (weeklyGoal) {
            3    -> "A great start — even 3 days a week builds lasting knowledge."
            5    -> "The scholar's path — five sessions a week for steady mastery."
            7    -> "Total dedication — daily practice unlocks deep instinctive fluency."
            else -> ""
        }
        AnimatedVisibility(visible = motivationText.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(StellaeShapes.md)
                    .background(Gold.copy(alpha = 0.08f))
                    .border(1.dp, Gold.copy(alpha = 0.25f), StellaeShapes.md)
                    .padding(14.dp)
            ) {
                Text(
                    text = motivationText,
                    style = StellaeTypography.bodyMd.copy(
                        color = TextSecondary,
                        lineHeight = 24.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
