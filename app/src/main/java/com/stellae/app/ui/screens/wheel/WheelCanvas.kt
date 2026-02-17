package com.stellae.app.ui.screens.wheel

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import com.stellae.app.domain.model.DignityType
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

// ── Domain data ───────────────────────────────────────────────────────────────

data class WheelState(
    val scale: Float = 1f,
    val rotation: Float = 0f,
    val selectedRing: Int? = null,
    val selectedSegment: Int? = null,
    val visibleRings: Set<String> = setOf(
        "zodiac", "domicile", "exaltation", "triplicity", "terms", "decans"
    )
)

data class SegmentInfo(
    val ringName: String,
    val signName: String,
    val planetName: String?,
    val planetGlyph: String?,
    val degreeRange: String?,
    val dignityType: DignityType?
)

// ── Astrological data tables ──────────────────────────────────────────────────

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

// Elements: 0=fire,1=earth,2=air,3=water (repeating pattern)
private val SIGN_ELEMENTS = arrayOf(
    "fire", "earth", "air", "water",
    "fire", "earth", "air", "water",
    "fire", "earth", "air", "water"
)

// Planet IDs: 1=Sun,2=Moon,3=Mercury,4=Venus,5=Mars,6=Jupiter,7=Saturn
// Each sign has a day ruler (index 0) and night ruler (index 1) — for domicile display, day ruler primary
private val DOMICILE_RULERS = arrayOf(5, 4, 3, 2, 1, 3, 4, 5, 6, 7, 7, 6)

// Exaltation: signId (0-based) -> Pair(planetId, degree)
private val EXALTATION_MAP = mapOf(
    0 to Pair(1, 19),   // Sun exalted in Aries at 19°
    1 to Pair(2, 3),    // Moon exalted in Taurus at 3°
    3 to Pair(6, 15),   // Jupiter exalted in Cancer at 15°
    5 to Pair(7, 27),   // Saturn (actually Mercury) — Mercury exalted in Virgo at 15°; keeping traditional: Mercury 15 Virgo
    6 to Pair(7, 21),   // Saturn exalted in Libra at 21°
    9 to Pair(5, 28),   // Mars exalted in Capricorn at 28°
    11 to Pair(4, 27)   // Venus exalted in Pisces at 27°
)
// Corrected exaltation map (Ptolemaic traditional)
private val EXALTATION_MAP_CORRECTED = mapOf(
    0 to Pair(1, 19),   // Sun exalted Aries 19°
    1 to Pair(2, 3),    // Moon exalted Taurus 3°
    3 to Pair(6, 15),   // Jupiter exalted Cancer 15°
    5 to Pair(3, 15),   // Mercury exalted Virgo 15°
    6 to Pair(7, 21),   // Saturn exalted Libra 21°
    9 to Pair(5, 28),   // Mars exalted Capricorn 28°
    11 to Pair(4, 27)   // Venus exalted Pisces 27°
)

private val PLANET_NAMES = arrayOf("", "Sun", "Moon", "Mercury", "Venus", "Mars", "Jupiter", "Saturn")
private val PLANET_GLYPHS = arrayOf("", "\u2609", "\u263D", "\u263F", "\u2640", "\u2642", "\u2643", "\u2644")

// Planet colors indexed by planet ID (0 unused)
private val PLANET_COLORS = arrayOf(
    Color.Transparent,            // 0 unused
    Color(0xFFD4A832),            // 1 Sun
    Color(0xFFB8C4D8),            // 2 Moon
    Color(0xFFC99A45),            // 3 Mercury
    Color(0xFF4CA77A),            // 4 Venus
    Color(0xFFC44545),            // 5 Mars
    Color(0xFF4A6FA5),            // 6 Jupiter
    Color(0xFF7A8899)             // 7 Saturn
)

private val ELEMENT_COLORS = mapOf(
    "fire" to Color(0xFFD45A30),
    "earth" to Color(0xFF6B8C42),
    "air" to Color(0xFF5A8EC9),
    "water" to Color(0xFF3A7AAA)
)

// Triplicity rulers: signIndex -> Triple(dayPlanetId, nightPlanetId, participatingPlanetId)
// Ptolemaic system: Fire=Sun/Jupiter/Saturn, Earth=Venus/Moon/Mars, Air=Saturn/Mercury/Jupiter, Water=Venus/Mars/Moon
private val TRIPLICITY_RULERS = arrayOf(
    Triple(1, 6, 7),  // Aries (fire)  — day=Sun, night=Jupiter, part=Saturn
    Triple(4, 2, 5),  // Taurus (earth)— day=Venus, night=Moon, part=Mars
    Triple(7, 3, 6),  // Gemini (air)  — day=Saturn, night=Mercury, part=Jupiter
    Triple(4, 5, 2),  // Cancer (water)— day=Venus, night=Mars, part=Moon
    Triple(1, 6, 7),  // Leo (fire)
    Triple(4, 2, 5),  // Virgo (earth)
    Triple(7, 3, 6),  // Libra (air)
    Triple(4, 5, 2),  // Scorpio (water)
    Triple(1, 6, 7),  // Sagittarius (fire)
    Triple(4, 2, 5),  // Capricorn (earth)
    Triple(7, 3, 6),  // Aquarius (air)
    Triple(4, 5, 2)   // Pisces (water)
)

// Egyptian Terms: list of (startDeg, endDeg, planetId) per sign (12 signs, 5 terms each)
// Degrees are sign-relative (0-29)
private val TERMS_DATA = arrayOf(
    // Aries
    arrayOf(intArrayOf(0, 6, 6), intArrayOf(6, 12, 4), intArrayOf(12, 20, 3), intArrayOf(20, 25, 5), intArrayOf(25, 30, 7)),
    // Taurus
    arrayOf(intArrayOf(0, 8, 4), intArrayOf(8, 14, 3), intArrayOf(14, 22, 6), intArrayOf(22, 27, 7), intArrayOf(27, 30, 5)),
    // Gemini
    arrayOf(intArrayOf(0, 6, 3), intArrayOf(6, 12, 6), intArrayOf(12, 17, 4), intArrayOf(17, 24, 5), intArrayOf(24, 30, 7)),
    // Cancer
    arrayOf(intArrayOf(0, 7, 5), intArrayOf(7, 13, 4), intArrayOf(13, 19, 3), intArrayOf(19, 26, 6), intArrayOf(26, 30, 7)),
    // Leo
    arrayOf(intArrayOf(0, 6, 6), intArrayOf(6, 11, 4), intArrayOf(11, 18, 7), intArrayOf(18, 24, 3), intArrayOf(24, 30, 5)),
    // Virgo
    arrayOf(intArrayOf(0, 7, 3), intArrayOf(7, 17, 4), intArrayOf(17, 21, 6), intArrayOf(21, 28, 5), intArrayOf(28, 30, 7)),
    // Libra
    arrayOf(intArrayOf(0, 6, 7), intArrayOf(6, 14, 4), intArrayOf(14, 21, 6), intArrayOf(21, 28, 3), intArrayOf(28, 30, 5)),
    // Scorpio
    arrayOf(intArrayOf(0, 7, 5), intArrayOf(7, 11, 4), intArrayOf(11, 19, 3), intArrayOf(19, 24, 6), intArrayOf(24, 30, 7)),
    // Sagittarius
    arrayOf(intArrayOf(0, 12, 6), intArrayOf(12, 17, 4), intArrayOf(17, 21, 3), intArrayOf(21, 26, 7), intArrayOf(26, 30, 5)),
    // Capricorn
    arrayOf(intArrayOf(0, 7, 4), intArrayOf(7, 14, 6), intArrayOf(14, 22, 3), intArrayOf(22, 26, 7), intArrayOf(26, 30, 5)),
    // Aquarius
    arrayOf(intArrayOf(0, 7, 7), intArrayOf(7, 13, 3), intArrayOf(13, 20, 4), intArrayOf(20, 25, 6), intArrayOf(25, 30, 5)),
    // Pisces
    arrayOf(intArrayOf(0, 12, 4), intArrayOf(12, 16, 6), intArrayOf(16, 19, 3), intArrayOf(19, 28, 5), intArrayOf(28, 30, 7))
)

// Ptolemaic decans: signIndex -> [decan1PlanetId, decan2PlanetId, decan3PlanetId]
private val DECAN_RULERS = arrayOf(
    intArrayOf(5, 1, 4),   // Aries: Mars, Sun, Venus
    intArrayOf(3, 2, 7),   // Taurus: Mercury, Moon, Saturn
    intArrayOf(6, 5, 1),   // Gemini: Jupiter, Mars, Sun
    intArrayOf(4, 3, 2),   // Cancer: Venus, Mercury, Moon
    intArrayOf(7, 6, 5),   // Leo: Saturn, Jupiter, Mars
    intArrayOf(1, 4, 3),   // Virgo: Sun, Venus, Mercury
    intArrayOf(2, 7, 6),   // Libra: Moon, Saturn, Jupiter
    intArrayOf(5, 1, 4),   // Scorpio: Mars, Sun, Venus
    intArrayOf(3, 2, 7),   // Sagittarius: Mercury, Moon, Saturn
    intArrayOf(6, 5, 1),   // Capricorn: Jupiter, Mars, Sun
    intArrayOf(4, 3, 2),   // Aquarius: Venus, Mercury, Moon
    intArrayOf(7, 6, 5)    // Pisces: Saturn, Jupiter, Mars
)

// ── Ring radius fractions ─────────────────────────────────────────────────────

private const val R_ZODIAC_OUT = 1.00f
private const val R_ZODIAC_IN  = 0.90f
private const val R_DOM_OUT    = 0.90f
private const val R_DOM_IN     = 0.80f
private const val R_EXA_OUT    = 0.80f
private const val R_EXA_IN     = 0.70f
private const val R_TRI_OUT    = 0.70f
private const val R_TRI_IN     = 0.58f
private const val R_TER_OUT    = 0.58f
private const val R_TER_IN     = 0.40f
private const val R_DEC_OUT    = 0.40f
private const val R_DEC_IN     = 0.25f

private const val SEG_PAD_DEG  = 0.8f  // angular padding between segments in degrees

// ── WheelCanvas ───────────────────────────────────────────────────────────────

/**
 * Interactive Wheel of Dignity — six concentric rings showing all Ptolemaic
 * essential dignity data for the 12 zodiac signs.
 *
 * Usage:
 *   WheelCanvas(
 *       wheelState = wheelState,
 *       onSegmentTapped = { info -> showTooltip(info) }
 *   )
 */
@Composable
fun WheelCanvas(
    wheelState: WheelState,
    onSegmentTapped: (SegmentInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(wheelState.scale) }
    var centerOffset by remember { mutableStateOf(Offset.Zero) }

    // Tracks canvas size set during draw
    var canvasSize by remember { mutableStateOf(Offset.Zero) }

    val transformModifier = modifier
        .fillMaxSize()
        .pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                scale = (scale * zoom).coerceIn(0.5f, 3.0f)
                centerOffset = Offset(
                    centerOffset.x + pan.x,
                    centerOffset.y + pan.y
                )
            }
        }
        .pointerInput(Unit) {
            detectTapGestures { tapOffset ->
                val cx = canvasSize.x / 2f + centerOffset.x
                val cy = canvasSize.y / 2f + centerOffset.y
                val baseRadius = min(canvasSize.x, canvasSize.y) / 2f * 0.92f * scale

                val dx = tapOffset.x - cx
                val dy = tapOffset.y - cy
                val dist = sqrt(dx * dx + dy * dy)
                val fraction = dist / baseRadius

                // Compute angle, normalized 0..360 with Aries (0°) at top (-90°)
                var angleDeg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat() + 90f
                if (angleDeg < 0f) angleDeg += 360f
                if (angleDeg >= 360f) angleDeg -= 360f

                val info = hitTest(fraction, angleDeg, wheelState.visibleRings)
                if (info != null) onSegmentTapped(info)
            }
        }

    Canvas(modifier = transformModifier) {
        canvasSize = Offset(size.width, size.height)

        val cx = size.width / 2f + centerOffset.x
        val cy = size.height / 2f + centerOffset.y
        val baseRadius = min(size.width, size.height) / 2f * 0.92f * scale

        if ("zodiac" in wheelState.visibleRings) {
            drawZodiacRing(cx, cy, baseRadius)
        }
        if ("domicile" in wheelState.visibleRings) {
            drawDomicileRing(cx, cy, baseRadius)
        }
        if ("exaltation" in wheelState.visibleRings) {
            drawExaltationRing(cx, cy, baseRadius)
        }
        if ("triplicity" in wheelState.visibleRings) {
            drawTriplicityRing(cx, cy, baseRadius)
        }
        if ("terms" in wheelState.visibleRings) {
            drawTermsRing(cx, cy, baseRadius)
        }
        if ("decans" in wheelState.visibleRings) {
            drawDecansRing(cx, cy, baseRadius)
        }

        // Centre circle background
        drawCircle(
            color = Color(0xFF0B0E1A),
            radius = baseRadius * R_DEC_IN,
            center = Offset(cx, cy)
        )
        drawCircle(
            color = Color(0x40C9A76A),
            radius = baseRadius * R_DEC_IN,
            center = Offset(cx, cy),
            style = Stroke(width = 1.5f)
        )
    }
}

// ── Hit testing ───────────────────────────────────────────────────────────────

private fun hitTest(
    radiusFraction: Float,
    angleDeg: Float,
    visibleRings: Set<String>
): SegmentInfo? {
    val signIndex = (angleDeg / 30f).toInt().coerceIn(0, 11)
    val degInSign = angleDeg - signIndex * 30f

    return when {
        radiusFraction in R_ZODIAC_IN..R_ZODIAC_OUT && "zodiac" in visibleRings -> {
            SegmentInfo(
                ringName = "Zodiac",
                signName = SIGN_NAMES[signIndex],
                planetName = null,
                planetGlyph = SIGN_GLYPHS[signIndex],
                degreeRange = "0°–30°",
                dignityType = null
            )
        }
        radiusFraction in R_DOM_IN..R_DOM_OUT && "domicile" in visibleRings -> {
            val pid = DOMICILE_RULERS[signIndex]
            SegmentInfo(
                ringName = "Domicile",
                signName = SIGN_NAMES[signIndex],
                planetName = PLANET_NAMES[pid],
                planetGlyph = PLANET_GLYPHS[pid],
                degreeRange = null,
                dignityType = DignityType.DOMICILE
            )
        }
        radiusFraction in R_EXA_IN..R_EXA_OUT && "exaltation" in visibleRings -> {
            val exalt = EXALTATION_MAP_CORRECTED[signIndex]
            SegmentInfo(
                ringName = "Exaltation",
                signName = SIGN_NAMES[signIndex],
                planetName = exalt?.let { PLANET_NAMES[it.first] },
                planetGlyph = exalt?.let { PLANET_GLYPHS[it.first] },
                degreeRange = exalt?.let { "${it.second}°" },
                dignityType = if (exalt != null) DignityType.EXALTATION else null
            )
        }
        radiusFraction in R_TRI_IN..R_TRI_OUT && "triplicity" in visibleRings -> {
            val tri = TRIPLICITY_RULERS[signIndex]
            SegmentInfo(
                ringName = "Triplicity",
                signName = SIGN_NAMES[signIndex],
                planetName = "D:${PLANET_NAMES[tri.first]} N:${PLANET_NAMES[tri.second]}",
                planetGlyph = "${PLANET_GLYPHS[tri.first]}/${PLANET_GLYPHS[tri.second]}",
                degreeRange = null,
                dignityType = DignityType.TRIPLICITY
            )
        }
        radiusFraction in R_TER_IN..R_TER_OUT && "terms" in visibleRings -> {
            val terms = TERMS_DATA[signIndex]
            val termEntry = terms.firstOrNull { degInSign >= it[0] && degInSign < it[1] }
            val pid = termEntry?.get(2) ?: 0
            SegmentInfo(
                ringName = "Terms",
                signName = SIGN_NAMES[signIndex],
                planetName = if (pid > 0) PLANET_NAMES[pid] else null,
                planetGlyph = if (pid > 0) PLANET_GLYPHS[pid] else null,
                degreeRange = termEntry?.let { "${it[0]}°–${it[1]}°" },
                dignityType = DignityType.TERM
            )
        }
        radiusFraction in R_DEC_IN..R_DEC_OUT && "decans" in visibleRings -> {
            val decanIndex = (degInSign / 10f).toInt().coerceIn(0, 2)
            val pid = DECAN_RULERS[signIndex][decanIndex]
            val start = decanIndex * 10
            SegmentInfo(
                ringName = "Decan",
                signName = SIGN_NAMES[signIndex],
                planetName = PLANET_NAMES[pid],
                planetGlyph = PLANET_GLYPHS[pid],
                degreeRange = "$start°–${start + 10}°",
                dignityType = DignityType.DECAN
            )
        }
        else -> null
    }
}

// ── Ring draw helpers ─────────────────────────────────────────────────────────

private fun DrawScope.drawZodiacRing(cx: Float, cy: Float, baseRadius: Float) {
    val outerR = baseRadius * R_ZODIAC_OUT
    val innerR = baseRadius * R_ZODIAC_IN

    for (i in 0..11) {
        val startAngle = i * 30f - 90f + SEG_PAD_DEG
        val sweepAngle = 30f - SEG_PAD_DEG * 2f
        val color = ELEMENT_COLORS[SIGN_ELEMENTS[i]] ?: Color.Gray

        drawAnnularSegment(cx, cy, innerR, outerR, startAngle, sweepAngle, color.copy(alpha = 0.75f))

        // Draw ring border
        drawAnnularSegmentBorder(cx, cy, innerR, outerR, startAngle, sweepAngle)

        // Draw glyph
        val midAngle = startAngle + sweepAngle / 2f
        val midR = (innerR + outerR) / 2f
        val glyphX = cx + cos(Math.toRadians(midAngle.toDouble())).toFloat() * midR
        val glyphY = cy + sin(Math.toRadians(midAngle.toDouble())).toFloat() * midR

        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                textSize = (outerR - innerR) * 0.55f
                textAlign = Paint.Align.CENTER
                setColor(Color.White.copy(alpha = 0.95f).toArgb())
                isAntiAlias = true
                typeface = Typeface.DEFAULT_BOLD
            }
            drawText(SIGN_GLYPHS[i], glyphX, glyphY + paint.textSize / 3f, paint)
        }
    }
}

private fun DrawScope.drawDomicileRing(cx: Float, cy: Float, baseRadius: Float) {
    val outerR = baseRadius * R_DOM_OUT
    val innerR = baseRadius * R_DOM_IN

    for (i in 0..11) {
        val startAngle = i * 30f - 90f + SEG_PAD_DEG
        val sweepAngle = 30f - SEG_PAD_DEG * 2f
        val pid = DOMICILE_RULERS[i]
        val color = PLANET_COLORS[pid]

        drawAnnularSegment(cx, cy, innerR, outerR, startAngle, sweepAngle, color.copy(alpha = 0.70f))
        drawAnnularSegmentBorder(cx, cy, innerR, outerR, startAngle, sweepAngle)

        val midAngle = startAngle + sweepAngle / 2f
        val midR = (innerR + outerR) / 2f
        val gx = cx + cos(Math.toRadians(midAngle.toDouble())).toFloat() * midR
        val gy = cy + sin(Math.toRadians(midAngle.toDouble())).toFloat() * midR

        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                textSize = (outerR - innerR) * 0.50f
                textAlign = Paint.Align.CENTER
                this.color = Color.White.copy(alpha = 0.9f).toArgb()
                isAntiAlias = true
            }
            drawText(PLANET_GLYPHS[pid], gx, gy + paint.textSize / 3f, paint)
        }
    }
}

private fun DrawScope.drawExaltationRing(cx: Float, cy: Float, baseRadius: Float) {
    val outerR = baseRadius * R_EXA_OUT
    val innerR = baseRadius * R_EXA_IN

    for (i in 0..11) {
        val startAngle = i * 30f - 90f + SEG_PAD_DEG
        val sweepAngle = 30f - SEG_PAD_DEG * 2f
        val exalt = EXALTATION_MAP_CORRECTED[i]

        val color = if (exalt != null) {
            PLANET_COLORS[exalt.first].copy(alpha = 0.65f)
        } else {
            Color(0xFF1A2038).copy(alpha = 0.5f)
        }

        drawAnnularSegment(cx, cy, innerR, outerR, startAngle, sweepAngle, color)
        drawAnnularSegmentBorder(cx, cy, innerR, outerR, startAngle, sweepAngle)

        if (exalt != null) {
            val midAngle = startAngle + sweepAngle / 2f
            val midR = (innerR + outerR) / 2f
            val gx = cx + cos(Math.toRadians(midAngle.toDouble())).toFloat() * midR
            val gy = cy + sin(Math.toRadians(midAngle.toDouble())).toFloat() * midR

            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    textSize = (outerR - innerR) * 0.48f
                    textAlign = Paint.Align.CENTER
                    setColor(Color.White.copy(alpha = 0.9f).toArgb())
                    isAntiAlias = true
                }
                drawText(PLANET_GLYPHS[exalt.first], gx, gy + paint.textSize / 3f, paint)
            }
        }
    }
}

private fun DrawScope.drawTriplicityRing(cx: Float, cy: Float, baseRadius: Float) {
    val outerR = baseRadius * R_TRI_OUT
    val innerR = baseRadius * R_TRI_IN

    for (i in 0..11) {
        val startAngle = i * 30f - 90f + SEG_PAD_DEG
        val sweepAngle = 30f - SEG_PAD_DEG * 2f
        val element = SIGN_ELEMENTS[i]
        val color = (ELEMENT_COLORS[element] ?: Color.Gray).copy(alpha = 0.55f)

        drawAnnularSegment(cx, cy, innerR, outerR, startAngle, sweepAngle, color)
        drawAnnularSegmentBorder(cx, cy, innerR, outerR, startAngle, sweepAngle)

        val tri = TRIPLICITY_RULERS[i]
        val midAngle = startAngle + sweepAngle / 2f
        val midR = (innerR + outerR) / 2f
        val gx = cx + cos(Math.toRadians(midAngle.toDouble())).toFloat() * midR
        val gy = cy + sin(Math.toRadians(midAngle.toDouble())).toFloat() * midR

        // Draw D/N glyphs stacked vertically in the segment
        val ringHeight = outerR - innerR
        val textSize = ringHeight * 0.22f

        drawContext.canvas.nativeCanvas.apply {
            val paint = Paint().apply {
                this.textSize = textSize
                textAlign = Paint.Align.CENTER
                this.color = Color.White.copy(alpha = 0.85f).toArgb()
                isAntiAlias = true
            }
            val step = textSize * 1.1f
            drawText("D:" + PLANET_GLYPHS[tri.first], gx, gy - step + textSize / 3f, paint)
            drawText("N:" + PLANET_GLYPHS[tri.second], gx, gy + textSize / 3f, paint)
            drawText("P:" + PLANET_GLYPHS[tri.third], gx, gy + step + textSize / 3f, paint)
        }
    }
}

private fun DrawScope.drawTermsRing(cx: Float, cy: Float, baseRadius: Float) {
    val outerR = baseRadius * R_TER_OUT
    val innerR = baseRadius * R_TER_IN

    for (signIdx in 0..11) {
        val signStartDeg = signIdx * 30f
        val terms = TERMS_DATA[signIdx]

        for (term in terms) {
            val termStart = term[0].toFloat()
            val termEnd = term[1].toFloat()
            val pid = term[2]

            val startAngle = (signStartDeg + termStart) - 90f + SEG_PAD_DEG * 0.5f
            val sweepAngle = (termEnd - termStart) - SEG_PAD_DEG

            val color = PLANET_COLORS[pid].copy(alpha = 0.65f)
            drawAnnularSegment(cx, cy, innerR, outerR, startAngle, sweepAngle, color)
            drawAnnularSegmentBorder(cx, cy, innerR, outerR, startAngle, sweepAngle)

            // Only draw glyph if segment is wide enough
            if (sweepAngle > 3f) {
                val midAngle = startAngle + sweepAngle / 2f
                val midR = (innerR + outerR) / 2f
                val gx = cx + cos(Math.toRadians(midAngle.toDouble())).toFloat() * midR
                val gy = cy + sin(Math.toRadians(midAngle.toDouble())).toFloat() * midR
                val textSize = (outerR - innerR) * 0.38f

                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().apply {
                        this.textSize = textSize
                        textAlign = Paint.Align.CENTER
                        this.color = Color.White.copy(alpha = 0.90f).toArgb()
                        isAntiAlias = true
                    }
                    drawText(PLANET_GLYPHS[pid], gx, gy + paint.textSize / 3f, paint)
                }
            }
        }
    }
}

private fun DrawScope.drawDecansRing(cx: Float, cy: Float, baseRadius: Float) {
    val outerR = baseRadius * R_DEC_OUT
    val innerR = baseRadius * R_DEC_IN

    for (signIdx in 0..11) {
        val signStartDeg = signIdx * 30f
        for (decanIdx in 0..2) {
            val decanStartDeg = decanIdx * 10f
            val startAngle = (signStartDeg + decanStartDeg) - 90f + SEG_PAD_DEG
            val sweepAngle = 10f - SEG_PAD_DEG * 2f

            val pid = DECAN_RULERS[signIdx][decanIdx]
            val color = PLANET_COLORS[pid].copy(alpha = 0.70f)

            drawAnnularSegment(cx, cy, innerR, outerR, startAngle, sweepAngle, color)
            drawAnnularSegmentBorder(cx, cy, innerR, outerR, startAngle, sweepAngle)

            val midAngle = startAngle + sweepAngle / 2f
            val midR = (innerR + outerR) / 2f
            val gx = cx + cos(Math.toRadians(midAngle.toDouble())).toFloat() * midR
            val gy = cy + sin(Math.toRadians(midAngle.toDouble())).toFloat() * midR

            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    textSize = (outerR - innerR) * 0.42f
                    textAlign = Paint.Align.CENTER
                    this.color = Color.White.copy(alpha = 0.90f).toArgb()
                    isAntiAlias = true
                }
                drawText(PLANET_GLYPHS[pid], gx, gy + paint.textSize / 3f, paint)
            }
        }
    }
}

// ── Annular segment primitives ────────────────────────────────────────────────

/**
 * Draws a filled annular (ring) segment using a filled arc approach.
 * We draw the outer arc filled, then overdraw the inner area with background.
 */
private fun DrawScope.drawAnnularSegment(
    cx: Float,
    cy: Float,
    innerR: Float,
    outerR: Float,
    startAngleDeg: Float,
    sweepAngleDeg: Float,
    color: Color
) {
    // Filled outer arc
    drawArc(
        color = color,
        startAngle = startAngleDeg,
        sweepAngle = sweepAngleDeg,
        useCenter = true,
        topLeft = Offset(cx - outerR, cy - outerR),
        size = androidx.compose.ui.geometry.Size(outerR * 2, outerR * 2)
    )
    // Cut out inner with background colour
    drawArc(
        color = Color(0xFF0B0E1A),
        startAngle = startAngleDeg - 0.1f,
        sweepAngle = sweepAngleDeg + 0.2f,
        useCenter = true,
        topLeft = Offset(cx - innerR, cy - innerR),
        size = androidx.compose.ui.geometry.Size(innerR * 2, innerR * 2)
    )
}

private fun DrawScope.drawAnnularSegmentBorder(
    cx: Float,
    cy: Float,
    innerR: Float,
    outerR: Float,
    startAngleDeg: Float,
    sweepAngleDeg: Float
) {
    val borderColor = Color(0x33FFFFFF)
    // Outer arc border
    drawArc(
        color = borderColor,
        startAngle = startAngleDeg,
        sweepAngle = sweepAngleDeg,
        useCenter = false,
        topLeft = Offset(cx - outerR, cy - outerR),
        size = androidx.compose.ui.geometry.Size(outerR * 2, outerR * 2),
        style = Stroke(width = 0.8f)
    )
    // Inner arc border
    drawArc(
        color = borderColor,
        startAngle = startAngleDeg,
        sweepAngle = sweepAngleDeg,
        useCenter = false,
        topLeft = Offset(cx - innerR, cy - innerR),
        size = androidx.compose.ui.geometry.Size(innerR * 2, innerR * 2),
        style = Stroke(width = 0.8f)
    )
    // Radial end lines
    val startRad = Math.toRadians(startAngleDeg.toDouble())
    val endRad = Math.toRadians((startAngleDeg + sweepAngleDeg).toDouble())

    drawLine(
        color = borderColor,
        start = Offset(
            cx + cos(startRad).toFloat() * innerR,
            cy + sin(startRad).toFloat() * innerR
        ),
        end = Offset(
            cx + cos(startRad).toFloat() * outerR,
            cy + sin(startRad).toFloat() * outerR
        ),
        strokeWidth = 0.8f
    )
    drawLine(
        color = borderColor,
        start = Offset(
            cx + cos(endRad).toFloat() * innerR,
            cy + sin(endRad).toFloat() * innerR
        ),
        end = Offset(
            cx + cos(endRad).toFloat() * outerR,
            cy + sin(endRad).toFloat() * outerR
        ),
        strokeWidth = 0.8f
    )
}
