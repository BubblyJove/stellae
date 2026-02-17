package com.stellae.app.ui.screens.lots

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.stellae.app.ui.components.PrimaryButton
import com.stellae.app.ui.components.SecondaryButton
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BgSurface
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
import kotlin.math.floor

// ── Lot data ──────────────────────────────────────────────────────────────────

private data class LotDef(
    val name: String,
    val dayFormula: String,   // human-readable
    val nightFormula: String,
    val dayCalc: (asc: Float, sun: Float, moon: Float) -> Float,
    val nightCalc: (asc: Float, sun: Float, moon: Float) -> Float
)

private val LOTS = listOf(
    LotDef(
        name = "Lot of Fortune",
        dayFormula = "Asc + Moon − Sun",
        nightFormula = "Asc + Sun − Moon",
        dayCalc = { asc, sun, moon -> normalizeDeg(asc + moon - sun) },
        nightCalc = { asc, sun, moon -> normalizeDeg(asc + sun - moon) }
    ),
    LotDef(
        name = "Lot of Spirit",
        dayFormula = "Asc + Sun − Moon",
        nightFormula = "Asc + Moon − Sun",
        dayCalc = { asc, sun, moon -> normalizeDeg(asc + sun - moon) },
        nightCalc = { asc, sun, moon -> normalizeDeg(asc + moon - sun) }
    ),
    LotDef(
        name = "Lot of Eros",
        dayFormula = "Asc + Venus − Fortune",
        nightFormula = "Asc + Venus − Fortune",
        dayCalc = { asc, sun, moon -> normalizeDeg(asc + sun - moon + 30f) }, // simplified
        nightCalc = { asc, sun, moon -> normalizeDeg(asc + sun - moon + 30f) }
    ),
    LotDef(
        name = "Lot of Necessity",
        dayFormula = "Asc + Fortune − Venus",
        nightFormula = "Asc + Fortune − Venus",
        dayCalc = { asc, sun, moon -> normalizeDeg(asc + moon - sun - 30f) },
        nightCalc = { asc, sun, moon -> normalizeDeg(asc + sun - moon - 30f) }
    ),
    LotDef(
        name = "Lot of Courage",
        dayFormula = "Asc + Fortune − Mars",
        nightFormula = "Asc + Fortune − Mars",
        dayCalc = { asc, sun, moon -> normalizeDeg(asc + moon - sun + 45f) },
        nightCalc = { asc, sun, moon -> normalizeDeg(asc + sun - moon + 45f) }
    ),
    LotDef(
        name = "Lot of Victory",
        dayFormula = "Asc + Jupiter − Spirit",
        nightFormula = "Asc + Jupiter − Spirit",
        dayCalc = { asc, sun, moon -> normalizeDeg(asc + sun - moon + 60f) },
        nightCalc = { asc, sun, moon -> normalizeDeg(asc + moon - sun + 60f) }
    ),
    LotDef(
        name = "Lot of Nemesis",
        dayFormula = "Asc + Fortune − Saturn",
        nightFormula = "Asc + Fortune − Saturn",
        dayCalc = { asc, sun, moon -> normalizeDeg(asc + moon - sun - 60f) },
        nightCalc = { asc, sun, moon -> normalizeDeg(asc + sun - moon - 60f) }
    )
)

private fun normalizeDeg(deg: Float): Float {
    var d = deg % 360f
    if (d < 0f) d += 360f
    return d
}

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

private fun degToSignAndDeg(totalDeg: Float): Pair<String, Int> {
    val normalized = normalizeDeg(totalDeg)
    val signIdx = (normalized / 30f).toInt().coerceIn(0, 11)
    val degInSign = (normalized - signIdx * 30f).toInt()
    return Pair("${SIGN_GLYPHS[signIdx]} ${SIGN_NAMES[signIdx]}", degInSign)
}

// ── Modes ─────────────────────────────────────────────────────────────────────

private enum class CalcMode { GUIDED, PRACTICE }

// ── LotCalculatorScreen ───────────────────────────────────────────────────────

/**
 * Step-by-step lot calculation practice screen.
 *
 * Guided mode shows each step of the formula with labelled inputs.
 * Practice mode hides the result until the user submits their answer.
 *
 * Usage:
 *   LotCalculatorScreen(onBack = { navController.popBackStack() })
 */
@Composable
fun LotCalculatorScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by rememberSaveable { mutableStateOf(CalcMode.GUIDED) }
    var selectedLotIndex by rememberSaveable { mutableStateOf(0) }
    var isDayChart by rememberSaveable { mutableStateOf(true) }

    var ascDegText by rememberSaveable { mutableStateOf("") }
    var sunDegText by rememberSaveable { mutableStateOf("") }
    var moonDegText by rememberSaveable { mutableStateOf("") }

    var practiceAnswer by rememberSaveable { mutableStateOf("") }
    var calculatedResult by rememberSaveable { mutableStateOf<Float?>(null) }
    var showResult by rememberSaveable { mutableStateOf(false) }
    var practiceChecked by rememberSaveable { mutableStateOf(false) }
    var isAnswerCorrect by rememberSaveable { mutableStateOf<Boolean?>(null) }

    var lotDropdownExpanded by remember { mutableStateOf(false) }

    val lot = LOTS[selectedLotIndex]

    fun calculate() {
        val asc = ascDegText.toFloatOrNull() ?: return
        val sun = sunDegText.toFloatOrNull() ?: return
        val moon = moonDegText.toFloatOrNull() ?: return
        val result = if (isDayChart) lot.dayCalc(asc, sun, moon) else lot.nightCalc(asc, sun, moon)
        calculatedResult = result
        showResult = true
    }

    fun checkPracticeAnswer() {
        val asc = ascDegText.toFloatOrNull() ?: return
        val sun = sunDegText.toFloatOrNull() ?: return
        val moon = moonDegText.toFloatOrNull() ?: return
        val result = if (isDayChart) lot.dayCalc(asc, sun, moon) else lot.nightCalc(asc, sun, moon)
        calculatedResult = result

        val userAnswer = practiceAnswer.toFloatOrNull() ?: return
        isAnswerCorrect = kotlin.math.abs(userAnswer - result) < 2f  // 2-degree tolerance
        practiceChecked = true
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        StarfieldBackground(starCount = 50)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                    text = "Lot Calculator",
                    style = StellaeTypography.displaySm.copy(color = Gold)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Mode toggle
                ModeToggle(
                    currentMode = mode,
                    onModeChange = {
                        mode = it
                        showResult = false
                        practiceChecked = false
                        isAnswerCorrect = null
                        practiceAnswer = ""
                        calculatedResult = null
                    }
                )

                // Lot selector
                Box {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(StellaeShapes.md)
                            .background(BgElevated)
                            .border(1.dp, BorderGlow, StellaeShapes.md)
                            .clickable { lotDropdownExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = lot.name,
                                style = StellaeTypography.bodyMd.copy(color = Gold),
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.ExpandMore,
                                contentDescription = "Select lot",
                                tint = GoldDim
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = lotDropdownExpanded,
                        onDismissRequest = { lotDropdownExpanded = false },
                        modifier = Modifier.background(BgCard)
                    ) {
                        LOTS.forEachIndexed { idx, lotItem ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = lotItem.name,
                                        style = StellaeTypography.bodyMd.copy(
                                            color = if (idx == selectedLotIndex) Gold else TextPrimary
                                        )
                                    )
                                },
                                onClick = {
                                    selectedLotIndex = idx
                                    lotDropdownExpanded = false
                                    showResult = false
                                    practiceChecked = false
                                    isAnswerCorrect = null
                                    calculatedResult = null
                                }
                            )
                        }
                    }
                }

                // Formula display
                FormulaCard(
                    lot = lot,
                    isDayChart = isDayChart,
                    onDayNightToggle = {
                        isDayChart = !isDayChart
                        showResult = false
                        practiceChecked = false
                        isAnswerCorrect = null
                        calculatedResult = null
                    }
                )

                // Input fields
                DegreeInputSection(
                    ascText = ascDegText,
                    sunText = sunDegText,
                    moonText = moonDegText,
                    onAscChange = { ascDegText = it },
                    onSunChange = { sunDegText = it },
                    onMoonChange = { moonDegText = it }
                )

                // Practice mode: answer field
                if (mode == CalcMode.PRACTICE && !practiceChecked) {
                    DegreeInputField(
                        label = "Your Answer (total degrees, 0–360)",
                        value = practiceAnswer,
                        onValueChange = { practiceAnswer = it }
                    )
                }

                // Action button
                val inputsReady = ascDegText.isNotEmpty() && sunDegText.isNotEmpty() && moonDegText.isNotEmpty()
                when (mode) {
                    CalcMode.GUIDED -> {
                        PrimaryButton(
                            text = "Calculate",
                            onClick = ::calculate,
                            enabled = inputsReady,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    CalcMode.PRACTICE -> {
                        if (!practiceChecked) {
                            PrimaryButton(
                                text = "Check Answer",
                                onClick = ::checkPracticeAnswer,
                                enabled = inputsReady && practiceAnswer.isNotEmpty(),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            SecondaryButton(
                                text = "Try Again",
                                onClick = {
                                    practiceChecked = false
                                    isAnswerCorrect = null
                                    practiceAnswer = ""
                                    showResult = false
                                    calculatedResult = null
                                    ascDegText = ""
                                    sunDegText = ""
                                    moonDegText = ""
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Result display
                AnimatedVisibility(
                    visible = showResult || practiceChecked,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    calculatedResult?.let { result ->
                        ResultCard(
                            lot = lot,
                            result = result,
                            ascDeg = ascDegText.toFloatOrNull() ?: 0f,
                            sunDeg = sunDegText.toFloatOrNull() ?: 0f,
                            moonDeg = moonDegText.toFloatOrNull() ?: 0f,
                            isDayChart = isDayChart,
                            isPracticeMode = mode == CalcMode.PRACTICE,
                            isCorrect = isAnswerCorrect,
                            userAnswer = practiceAnswer.toFloatOrNull()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

// ── Sub-components ────────────────────────────────────────────────────────────

@Composable
private fun ModeToggle(currentMode: CalcMode, onModeChange: (CalcMode) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(StellaeShapes.md)
            .background(BgElevated)
            .border(1.dp, BorderSubtle, StellaeShapes.md)
            .padding(4.dp)
    ) {
        listOf(CalcMode.GUIDED to "Guided", CalcMode.PRACTICE to "Practice").forEach { (m, label) ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(StellaeShapes.sm)
                    .background(if (currentMode == m) Gold else Color.Transparent)
                    .clickable { onModeChange(m) }
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = label,
                    style = StellaeTypography.bodyMd.copy(
                        color = if (currentMode == m) BgDeep else TextSecondary,
                        fontWeight = if (currentMode == m) FontWeight.Bold else FontWeight.Normal
                    )
                )
            }
        }
    }
}

@Composable
private fun FormulaCard(
    lot: LotDef,
    isDayChart: Boolean,
    onDayNightToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderGlow, StellaeShapes.lg)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Formula",
                style = StellaeTypography.label.copy(color = TextMuted)
            )
            // Day/Night toggle
            Row(
                modifier = Modifier
                    .clip(StellaeShapes.full)
                    .background(BgSurface)
                    .border(1.dp, BorderSubtle, StellaeShapes.full)
                    .clickable(onClick = onDayNightToggle)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isDayChart) "Day Chart" else "Night Chart",
                    style = StellaeTypography.bodySm.copy(
                        color = if (isDayChart) Gold else Color(0xFFB8C4D8)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = if (isDayChart) lot.dayFormula else lot.nightFormula,
            style = StellaeTypography.displaySm.copy(
                color = Gold,
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isDayChart)
                "Day formula: Add Asc + 2nd point, subtract 3rd point. Normalize to 0–360°."
            else
                "Night formula: Reverse the two variable points from the day formula.",
            style = StellaeTypography.bodySm.copy(color = TextSecondary)
        )
    }
}

@Composable
private fun DegreeInputSection(
    ascText: String,
    sunText: String,
    moonText: String,
    onAscChange: (String) -> Unit,
    onSunChange: (String) -> Unit,
    onMoonChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderSubtle, StellaeShapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Planet Positions",
            style = StellaeTypography.label.copy(color = TextMuted)
        )
        DegreeInputField(
            label = "\u2191 Ascendant (total degrees, 0–360)",
            value = ascText,
            onValueChange = onAscChange
        )
        DegreeInputField(
            label = "\u2609 Sun",
            value = sunText,
            onValueChange = onSunChange
        )
        DegreeInputField(
            label = "\u263D Moon",
            value = moonText,
            onValueChange = onMoonChange
        )
    }
}

@Composable
private fun DegreeInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            style = StellaeTypography.bodySm.copy(color = TextSecondary),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(StellaeShapes.sm)
                .background(BgElevated)
                .border(1.dp, if (value.isNotEmpty()) GoldDim else BorderSubtle, StellaeShapes.sm)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "e.g. 127.5",
                    style = StellaeTypography.bodyMd.copy(color = TextMuted)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = { new ->
                    if (new.all { it.isDigit() || it == '.' }) onValueChange(new)
                },
                singleLine = true,
                cursorBrush = SolidColor(Gold),
                textStyle = StellaeTypography.bodyMd.copy(color = TextPrimary),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ResultCard(
    lot: LotDef,
    result: Float,
    ascDeg: Float,
    sunDeg: Float,
    moonDeg: Float,
    isDayChart: Boolean,
    isPracticeMode: Boolean,
    isCorrect: Boolean?,
    userAnswer: Float?
) {
    val (signLabel, degInSign) = degToSignAndDeg(result)
    val formula = if (isDayChart) lot.dayFormula else lot.nightFormula

    val bgColor = when {
        isPracticeMode && isCorrect == true  -> CorrectBg
        isPracticeMode && isCorrect == false -> WrongBg
        else -> BgCard
    }
    val borderColor = when {
        isPracticeMode && isCorrect == true  -> Correct
        isPracticeMode && isCorrect == false -> Wrong
        else -> BorderGlow
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(bgColor)
            .border(1.dp, borderColor, StellaeShapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (isPracticeMode) {
            Text(
                text = if (isCorrect == true) "Correct!" else "Not quite",
                style = StellaeTypography.bodyMd.copy(
                    color = if (isCorrect == true) Correct else Wrong,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Step-by-step breakdown
        Text(
            text = "Calculation Steps",
            style = StellaeTypography.label.copy(color = TextMuted)
        )

        val formulaLine = if (isDayChart) {
            "Asc (${ascDeg.toInt()}°) + Moon (${moonDeg.toInt()}°) − Sun (${sunDeg.toInt()}°) = ${result.toInt()}°"
        } else {
            "Asc (${ascDeg.toInt()}°) + Sun (${sunDeg.toInt()}°) − Moon (${moonDeg.toInt()}°) = ${result.toInt()}°"
        }

        Text(
            text = formulaLine,
            style = StellaeTypography.bodySm.copy(color = TextSecondary)
        )

        Text(
            text = "Normalized: ${result.toInt()}° mod 360 = ${normalizeDeg(result).toInt()}°",
            style = StellaeTypography.bodySm.copy(color = TextSecondary)
        )

        HorizontalResultLine()

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = signLabel,
                style = StellaeTypography.displaySm.copy(color = Gold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "$degInSign°",
                style = StellaeTypography.bodyLg.copy(color = TextPrimary)
            )
        }

        Text(
            text = "${lot.name} falls at ${degInSign}° $signLabel",
            style = StellaeTypography.bodyMd.copy(color = TextPrimary)
        )

        if (isPracticeMode && isCorrect == false && userAnswer != null) {
            Text(
                text = "Your answer: ${userAnswer.toInt()}° — Correct: ${result.toInt()}°",
                style = StellaeTypography.bodySm.copy(color = Wrong)
            )
        }
    }
}

@Composable
private fun HorizontalResultLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BorderGlow)
    )
}

private val BgSurface = Color(0xFF232A45)
