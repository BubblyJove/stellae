package com.stellae.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stellae.app.ui.theme.Streak
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography

// ── StreakBadge ───────────────────────────────────────────────────────────────

/**
 * Compact badge displaying the current answer streak.
 *
 * Usage:
 *   StreakBadge(count = 7)
 */
@Composable
fun StreakBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    // Gradient: Streak at 15% opacity -> Streak at 5% opacity (simulates the 15%->5% CSS stops)
    val badgeGradient = Brush.horizontalGradient(
        colors = listOf(
            Streak.copy(alpha = 0.15f),
            Streak.copy(alpha = 0.05f),
        ),
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(StellaeShapes.full)
            .background(badgeGradient)
            .border(
                width = 1.dp,
                color = Streak.copy(alpha = 0.30f),
                shape = StellaeShapes.full,
            )
            .padding(vertical = 4.dp, horizontal = 12.dp),
    ) {
        Text(
            text  = "\uD83D\uDD25", // fire emoji
            style = StellaeTypography.bodySm,
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text  = count.toString(),
            style = StellaeTypography.bodySm.copy(
                fontWeight = FontWeight.Bold,
                color      = Streak,
            ),
        )
    }
}
