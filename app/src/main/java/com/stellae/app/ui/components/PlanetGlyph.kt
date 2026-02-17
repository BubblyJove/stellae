package com.stellae.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stellae.app.ui.theme.StellaeTypography

// ── PlanetGlyph ───────────────────────────────────────────────────────────────

/**
 * Circular container displaying a planetary glyph character.
 *
 * The glyph font size scales proportionally: at the default 40dp container size
 * the font is 20sp, scaling linearly with the [size] parameter.
 *
 * Usage:
 *   PlanetGlyph(glyph = "\u2609", color = StellaeTheme.colors.sun) // ☉ Sun
 *   PlanetGlyph(glyph = "\u263D", color = StellaeTheme.colors.moon, size = 56.dp) // ☽ Moon
 *
 * Common planetary Unicode glyphs:
 *   Sun ☉ \u2609  | Moon ☽ \u263D | Mercury ☿ \u263F
 *   Venus ♀ \u2640 | Mars ♂ \u2642 | Jupiter ♃ \u2643 | Saturn ♄ \u2644
 */
@Composable
fun PlanetGlyph(
    glyph: String,
    color: Color,
    size: Dp = 40.dp,
    modifier: Modifier = Modifier,
) {
    // Font size scales relative to default container size of 40dp.
    // At 40dp -> 20sp; at 56dp -> 28sp, etc.
    val glyphFontSize = (size.value * 0.5f).sp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.10f))
            .border(width = 2.dp, color = color, shape = CircleShape),
    ) {
        Text(
            text  = glyph,
            style = StellaeTypography.bodyLg.copy(
                color    = color,
                fontSize = glyphFontSize,
            ),
        )
    }
}
