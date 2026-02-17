package com.stellae.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin
import kotlin.random.Random

// ── Star data ─────────────────────────────────────────────────────────────────

private data class Star(
    /** Fractional position [0,1] in both axes — resolved to px at draw time */
    val xFraction: Float,
    val yFraction: Float,
    /** Radius in pixels */
    val radius: Float,
    /** Base opacity [0.2, 0.8] */
    val baseOpacity: Float,
    /** Twinkle angular speed (radians per millisecond) derived from period [3s, 8s] */
    val twinkleSpeed: Float,
    /** Phase offset so stars don't all pulse in sync */
    val phaseOffset: Float,
)

// ── StarfieldBackground ───────────────────────────────────────────────────────

/**
 * Full-bleed animated starfield canvas with a subtle central vignette.
 *
 * Each star twinkles independently using a sine-wave opacity animation driven
 * by [withFrameMillis] — no polling, no coroutine timers.
 *
 * Usage:
 *   Box {
 *       StarfieldBackground()
 *       // ... your content on top
 *   }
 */
@Composable
fun StarfieldBackground(
    starCount: Int = 100,
    modifier: Modifier = Modifier,
) {
    // Generate stars once per composition
    val stars = remember(starCount) {
        List(starCount) {
            val periodMs = Random.nextFloat() * (8000f - 3000f) + 3000f  // 3s..8s
            Star(
                xFraction    = Random.nextFloat(),
                yFraction    = Random.nextFloat(),
                // Radius 0.5dp..2dp — we store in logical dp units; scale at draw time
                radius       = Random.nextFloat() * 1.5f + 0.5f,
                baseOpacity  = Random.nextFloat() * 0.6f + 0.2f,
                twinkleSpeed = (2f * Math.PI / periodMs).toFloat(),
                phaseOffset  = Random.nextFloat() * (2f * Math.PI).toFloat(),
            )
        }
    }

    // Frame time in ms — updated every frame
    val timeMs = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        // Capture the start time once and measure elapsed ms from there
        val startMs = withFrameMillis { it }
        while (true) {
            withFrameMillis { frameMs ->
                timeMs.floatValue = (frameMs - startMs).toFloat()
            }
        }
    }

    // Force recomposition on every frame by reading timeMs.value
    val currentTime = timeMs.floatValue

    Canvas(modifier = modifier.fillMaxSize()) {
        val density = this.size          // DrawScope provides size

        // Draw stars
        stars.forEach { star ->
            val x = star.xFraction * size.width
            val y = star.yFraction * size.height

            val sinValue = sin(currentTime * star.twinkleSpeed + star.phaseOffset)
            val opacity  = (star.baseOpacity + sinValue.toFloat() * 0.3f).coerceIn(0f, 1f)

            // Convert logical dp radius -> px using display density ratio
            val radiusPx = star.radius * (size.minDimension / 400f).coerceAtLeast(1f)

            drawCircle(
                color  = Color.White.copy(alpha = opacity),
                radius = radiusPx,
                center = Offset(x, y),
            )
        }

        // Radial vignette: rgba(26,32,56,0.5) at center -> transparent at edge
        drawRect(
            brush = object : ShaderBrush() {
                override fun createShader(size: Size): android.graphics.Shader {
                    return RadialGradientShader(
                        center  = Offset(size.width / 2f, size.height / 2f),
                        radius  = size.maxDimension / 2f,
                        colors  = listOf(
                            Color(0x801A2038),  // rgba(26,32,56,0.5)
                            Color.Transparent,
                        ),
                        colorStops = listOf(0f, 1f),
                    )
                }
            },
            size = size,
        )
    }
}
