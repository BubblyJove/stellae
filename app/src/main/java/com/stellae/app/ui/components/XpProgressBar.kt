package com.stellae.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.stellae.app.ui.theme.BgSurface
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes

// ── XpProgressBar ─────────────────────────────────────────────────────────────

/**
 * Animated XP progress bar with a gold gradient fill.
 *
 * @param progress Value in [0f, 1f] representing fill fraction.
 *
 * Usage:
 *   XpProgressBar(progress = 0.65f)
 */
@Composable
fun XpProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    // Clamp to valid range
    val clampedProgress = progress.coerceIn(0f, 1f)

    // 800ms ease-in-out animation (approximates CSS cubic-bezier)
    val animatedProgress by animateFloatAsState(
        targetValue   = clampedProgress,
        animationSpec = tween(
            durationMillis = 800,
            easing         = FastOutSlowInEasing,
        ),
        label = "xpProgress",
    )

    // Gold gradient fill — horizontal, GoldDim (start) -> Gold (end), simulating 90deg
    val fillGradient = Brush.horizontalGradient(
        colors = listOf(GoldDim, Gold),
    )

    // Track
    Box(
        modifier = modifier
            .height(8.dp)
            .fillMaxWidth()
            .clip(StellaeShapes.full)
            .background(BgSurface),
    ) {
        // Filled portion
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = animatedProgress)
                .clip(StellaeShapes.full)
                .background(fillGradient),
        )
    }
}
