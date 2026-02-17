package com.stellae.app.ui.screens.speed

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellae.app.ui.components.PrimaryButton
import com.stellae.app.ui.components.SecondaryButton
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Correct
import com.stellae.app.ui.theme.CorrectBg
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary
import com.stellae.app.ui.theme.Wrong
import com.stellae.app.ui.theme.WrongBg
import kotlinx.coroutines.delay

// ── Speed drill data ──────────────────────────────────────────────────────────

private enum class SpeedMode(val label: String, val description: String) {
    DOMICILE("Domicile Speed",   "Identify planetary domiciles"),
    EXALTATION("Exaltation Speed", "Recall exaltation placements"),
    FULL("Full Speed",           "All dignity types mixed")
}

private data class SpeedQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int
)

private val DOMICILE_QUESTIONS = listOf(
    SpeedQuestion("Domicile of the Sun?",     listOf("Leo", "Aries", "Sagittarius", "Taurus"), 0),
    SpeedQuestion("Domicile of the Moon?",    listOf("Cancer", "Pisces", "Taurus", "Virgo"), 0),
    SpeedQuestion("Domicile of Mercury?",     listOf("Gemini & Virgo", "Aries & Scorpio", "Leo & Cancer", "Libra & Taurus"), 0),
    SpeedQuestion("Domicile of Venus?",       listOf("Taurus & Libra", "Gemini & Virgo", "Aries & Scorpio", "Leo & Aquarius"), 0),
    SpeedQuestion("Domicile of Mars?",        listOf("Aries & Scorpio", "Leo & Aquarius", "Gemini & Virgo", "Taurus & Libra"), 0),
    SpeedQuestion("Domicile of Jupiter?",     listOf("Sagittarius & Pisces", "Aries & Scorpio", "Leo & Aquarius", "Cancer & Capricorn"), 0),
    SpeedQuestion("Domicile of Saturn?",      listOf("Capricorn & Aquarius", "Taurus & Libra", "Aries & Scorpio", "Cancer & Leo"), 0),
    SpeedQuestion("Ruler of Leo?",            listOf("Sun", "Mars", "Jupiter", "Moon"), 0),
    SpeedQuestion("Ruler of Cancer?",         listOf("Moon", "Venus", "Mercury", "Saturn"), 0),
    SpeedQuestion("Ruler of Scorpio (trad.)?", listOf("Mars", "Pluto", "Saturn", "Jupiter"), 0)
)

private val EXALTATION_QUESTIONS = listOf(
    SpeedQuestion("Sun is exalted in?",     listOf("Aries", "Leo", "Taurus", "Sagittarius"), 0),
    SpeedQuestion("Moon is exalted in?",    listOf("Taurus", "Cancer", "Pisces", "Virgo"), 0),
    SpeedQuestion("Jupiter is exalted in?", listOf("Cancer", "Sagittarius", "Pisces", "Leo"), 0),
    SpeedQuestion("Mercury is exalted in?", listOf("Virgo", "Gemini", "Capricorn", "Libra"), 0),
    SpeedQuestion("Saturn is exalted in?",  listOf("Libra", "Capricorn", "Aquarius", "Aries"), 0),
    SpeedQuestion("Mars is exalted in?",    listOf("Capricorn", "Aries", "Scorpio", "Taurus"), 0),
    SpeedQuestion("Venus is exalted in?",   listOf("Pisces", "Taurus", "Libra", "Gemini"), 0),
    SpeedQuestion("Sun exaltation degree?", listOf("19°", "9°", "27°", "3°"), 0),
    SpeedQuestion("Moon exaltation degree?", listOf("3°", "15°", "21°", "27°"), 0),
    SpeedQuestion("Saturn exaltation degree?", listOf("21°", "15°", "3°", "28°"), 0)
)

private val ALL_QUESTIONS = (DOMICILE_QUESTIONS + EXALTATION_QUESTIONS).shuffled()

private enum class ArenaPhase {
    MODE_SELECT, COUNTDOWN, PLAYING, RESULT
}

private const val FLASH_DURATION_MS = 600L
private const val QUESTIONS_PER_ROUND = 10

// ── SpeedArenaScreen ──────────────────────────────────────────────────────────

/**
 * Timed recognition drill — tap answers as fast as possible.
 * Features: 3-2-1 countdown, per-question color flash, result screen with
 * time and accuracy comparison.
 *
 * Usage:
 *   SpeedArenaScreen(onBack = { navController.popBackStack() })
 */
@Composable
fun SpeedArenaScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phase by remember { mutableStateOf(ArenaPhase.MODE_SELECT) }
    var selectedMode by remember { mutableStateOf(SpeedMode.DOMICILE) }
    var countdownValue by remember { mutableIntStateOf(3) }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var elapsedMs by remember { mutableFloatStateOf(0f) }
    var isRunning by remember { mutableStateOf(false) }

    val answers = remember { mutableStateListOf<Boolean>() }
    var flashState by remember { mutableStateOf<Boolean?>(null) } // null=none, true=correct, false=wrong

    val questions = remember(selectedMode) {
        when (selectedMode) {
            SpeedMode.DOMICILE   -> DOMICILE_QUESTIONS
            SpeedMode.EXALTATION -> EXALTATION_QUESTIONS
            SpeedMode.FULL       -> ALL_QUESTIONS
        }.take(QUESTIONS_PER_ROUND)
    }

    // Timer coroutine
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (isRunning) {
                delay(50L)
                elapsedMs += 50f
            }
        }
    }

    // Countdown coroutine
    LaunchedEffect(phase) {
        if (phase == ArenaPhase.COUNTDOWN) {
            for (i in 3 downTo 1) {
                countdownValue = i
                delay(1000L)
            }
            countdownValue = 0
            delay(300L)
            phase = ArenaPhase.PLAYING
            isRunning = true
        }
    }

    // Flash fade coroutine
    LaunchedEffect(flashState) {
        if (flashState != null) {
            delay(FLASH_DURATION_MS)
            flashState = null
        }
    }

    val flashBg by animateColorAsState(
        targetValue = when (flashState) {
            true  -> CorrectBg
            false -> WrongBg
            null  -> Color.Transparent
        },
        animationSpec = tween(durationMillis = 200),
        label = "flashBg"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
            .background(flashBg)
    ) {
        StarfieldBackground(starCount = 70)

        when (phase) {
            ArenaPhase.MODE_SELECT -> {
                ModeSelectContent(
                    selectedMode = selectedMode,
                    onModeSelect = { selectedMode = it },
                    onStart = {
                        answers.clear()
                        currentQuestionIndex = 0
                        elapsedMs = 0f
                        phase = ArenaPhase.COUNTDOWN
                    },
                    onBack = onBack
                )
            }

            ArenaPhase.COUNTDOWN -> {
                CountdownContent(value = countdownValue)
            }

            ArenaPhase.PLAYING -> {
                if (currentQuestionIndex < questions.size) {
                    PlayingContent(
                        question = questions[currentQuestionIndex],
                        questionNumber = currentQuestionIndex + 1,
                        totalQuestions = questions.size,
                        elapsedMs = elapsedMs,
                        onAnswer = { isCorrect ->
                            answers.add(isCorrect)
                            flashState = isCorrect
                            if (currentQuestionIndex + 1 >= questions.size) {
                                isRunning = false
                                phase = ArenaPhase.RESULT
                            } else {
                                currentQuestionIndex++
                            }
                        }
                    )
                }
            }

            ArenaPhase.RESULT -> {
                ResultContent(
                    mode = selectedMode,
                    answers = answers.toList(),
                    elapsedMs = elapsedMs,
                    onPlayAgain = {
                        answers.clear()
                        currentQuestionIndex = 0
                        elapsedMs = 0f
                        phase = ArenaPhase.COUNTDOWN
                    },
                    onBack = onBack
                )
            }
        }
    }
}

// ── Phase composables ─────────────────────────────────────────────────────────

@Composable
private fun ModeSelectContent(
    selectedMode: SpeedMode,
    onModeSelect: (SpeedMode) -> Unit,
    onStart: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Gold
                )
            }
            Text(
                text = "Speed Arena",
                style = StellaeTypography.displaySm.copy(color = Gold)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose Your Trial",
                style = StellaeTypography.displayMd.copy(color = TextPrimary),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Tap answers as fast as possible",
                style = StellaeTypography.bodyMd.copy(color = TextSecondary),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            SpeedMode.entries.forEach { mode ->
                val isSelected = mode == selectedMode
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(StellaeShapes.md)
                        .background(if (isSelected) Gold.copy(alpha = 0.15f) else BgCard)
                        .border(
                            1.dp,
                            if (isSelected) Gold else BorderSubtle,
                            StellaeShapes.md
                        )
                        .clickable { onModeSelect(mode) }
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Column {
                        Text(
                            text = mode.label,
                            style = StellaeTypography.bodyMd.copy(
                                color = if (isSelected) Gold else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = mode.description,
                            style = StellaeTypography.bodySm.copy(color = TextSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Start Drill",
                onClick = onStart,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "$QUESTIONS_PER_ROUND questions per round",
                style = StellaeTypography.bodySm.copy(color = TextMuted),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CountdownContent(value: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
                (fadeIn(tween(200)) + slideInVertically { it / 2 })
                    .togetherWith(fadeOut(tween(200)) + slideOutVertically { -it / 2 })
            },
            label = "countdown"
        ) { count ->
            Text(
                text = if (count > 0) count.toString() else "Go!",
                style = StellaeTypography.displayLg.copy(
                    color = if (count > 0) Gold else Correct,
                    fontWeight = FontWeight.Bold,
                    fontSize = 96.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun PlayingContent(
    question: SpeedQuestion,
    questionNumber: Int,
    totalQuestions: Int,
    elapsedMs: Float,
    onAnswer: (Boolean) -> Unit
) {
    val elapsedSec = elapsedMs / 1000f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header: progress + timer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            // Progress dots
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(totalQuestions) { idx ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(StellaeShapes.full)
                            .background(
                                when {
                                    idx < questionNumber - 1 -> Gold
                                    idx == questionNumber - 1 -> GoldDim
                                    else -> BgElevated
                                }
                            )
                    )
                }
            }

            // Timer display
            Box(
                modifier = Modifier
                    .clip(StellaeShapes.full)
                    .background(BgCard)
                    .border(1.dp, BorderSubtle, StellaeShapes.full)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${"%.1f".format(elapsedSec)}s",
                    style = StellaeTypography.bodyMd.copy(
                        color = when {
                            elapsedSec < 30f -> Correct
                            elapsedSec < 60f -> Gold
                            else -> Wrong
                        },
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        // Question card
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(StellaeShapes.xl)
                .background(BgCard)
                .border(1.dp, BorderGlow, StellaeShapes.xl)
                .padding(24.dp)
        ) {
            Text(
                text = "$questionNumber / $totalQuestions",
                style = StellaeTypography.label.copy(color = TextMuted),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Text(
                text = question.question,
                style = StellaeTypography.displaySm.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )
        }

        // Answer options — large tap targets
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            question.options.forEachIndexed { idx, option ->
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(StellaeShapes.md)
                        .background(BgElevated)
                        .border(1.dp, BorderSubtle, StellaeShapes.md)
                        .clickable { onAnswer(idx == question.correctIndex) }
                        .padding(vertical = 20.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = option,
                        style = StellaeTypography.bodyLg.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultContent(
    mode: SpeedMode,
    answers: List<Boolean>,
    elapsedMs: Float,
    onPlayAgain: () -> Unit,
    onBack: () -> Unit
) {
    val correctCount = answers.count { it }
    val accuracy = if (answers.isNotEmpty()) correctCount.toFloat() / answers.size else 0f
    val elapsedSec = elapsedMs / 1000f
    // Simulated personal best — in production from UserRepository
    val personalBestSec = 24.5f
    val isNewBest = elapsedSec < personalBestSec && accuracy >= 0.90f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        if (isNewBest) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "New record",
                tint = Gold,
                modifier = Modifier.size(56.dp)
            )
            Text(
                text = "New Personal Best!",
                style = StellaeTypography.displaySm.copy(color = Gold),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = mode.label,
            style = StellaeTypography.bodyMd.copy(color = TextSecondary),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Stats row
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatBox(
                label = "Time",
                value = "${"%.1f".format(elapsedSec)}s",
                color = if (isNewBest) Gold else TextPrimary,
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Accuracy",
                value = "${(accuracy * 100).toInt()}%",
                color = when {
                    accuracy >= 0.90f -> Correct
                    accuracy >= 0.70f -> Gold
                    else -> Wrong
                },
                modifier = Modifier.weight(1f)
            )
            StatBox(
                label = "Correct",
                value = "$correctCount / ${answers.size}",
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personal best comparison
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(StellaeShapes.md)
                .background(BgCard)
                .border(1.dp, BorderSubtle, StellaeShapes.md)
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Personal Best",
                    style = StellaeTypography.bodyMd.copy(color = TextSecondary)
                )
                Text(
                    text = "${"%.1f".format(personalBestSec)}s",
                    style = StellaeTypography.bodyMd.copy(
                        color = GoldDim,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = "Play Again",
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        SecondaryButton(
            text = "Back to Menu",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(StellaeShapes.md)
            .background(BgCard)
            .border(1.dp, BorderSubtle, StellaeShapes.md)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(
            text = value,
            style = StellaeTypography.displaySm.copy(
                color = color,
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )
        Text(
            text = label,
            style = StellaeTypography.caption.copy(color = TextMuted),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
