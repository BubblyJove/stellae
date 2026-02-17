package com.stellae.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldBright
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeTypography

// ── Shared constants ─────────────────────────────────────────────────────────

private val ButtonShape = RoundedCornerShape(12.dp)

// ── PrimaryButton ─────────────────────────────────────────────────────────────

/**
 * Gold gradient primary action button.
 *
 * Usage:
 *   PrimaryButton(text = "Start Quiz", onClick = { /* ... */ })
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Simulate translateY(-1dp) lift on hover/focus by reducing shadow on press
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        animationSpec = tween(durationMillis = 120),
        label = "primaryButtonElevation",
    )

    // 135-degree diagonal gradient: GoldDim -> Gold -> GoldBright
    val gradient = Brush.linearGradient(
        colors = listOf(Gold, GoldBright),
    )

    val contentAlpha = if (enabled) 1f else 0.45f

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(
                elevation      = elevation,
                shape          = ButtonShape,
                ambientColor   = Gold.copy(alpha = 0.35f),
                spotColor      = Gold.copy(alpha = 0.45f),
            )
            .clip(ButtonShape)
            .then(
                if (enabled) Modifier.background(gradient)
                else Modifier.background(GoldDim.copy(alpha = 0.4f))
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                enabled           = enabled,
                onClick           = onClick,
            )
            .padding(vertical = 16.dp, horizontal = 32.dp),
    ) {
        Text(
            text      = text,
            style     = StellaeTypography.bodyMd.copy(
                fontWeight = FontWeight.Bold,
                color      = BgDeep.copy(alpha = contentAlpha),
            ),
        )
    }
}

// ── SecondaryButton ───────────────────────────────────────────────────────────

/**
 * Outlined secondary action button.
 *
 * Usage:
 *   SecondaryButton(text = "View Reference", onClick = { /* ... */ })
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(ButtonShape)
            .background(Color.Transparent)
            .border(
                border = BorderStroke(width = 1.dp, color = GoldDim),
                shape  = ButtonShape,
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 32.dp),
    ) {
        Text(
            text  = text,
            style = StellaeTypography.bodyMd.copy(
                fontWeight = FontWeight.Medium,
                color      = Gold,
            ),
        )
    }
}
