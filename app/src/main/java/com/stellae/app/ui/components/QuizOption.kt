package com.stellae.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Correct
import com.stellae.app.ui.theme.CorrectBg
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary
import com.stellae.app.ui.theme.Wrong
import com.stellae.app.ui.theme.WrongBg

// ── QuizOptionState ───────────────────────────────────────────────────────────

enum class QuizOptionState {
    DEFAULT,
    CORRECT,
    WRONG,
}

// ── QuizOption ────────────────────────────────────────────────────────────────

private val OptionShape = RoundedCornerShape(12.dp)

/**
 * A single answer option in a quiz question.
 *
 * Usage:
 *   QuizOption(
 *       letter = "A",
 *       text   = "Domicile",
 *       state  = QuizOptionState.CORRECT,
 *       onClick = { /* handle selection */ },
 *   )
 */
@Composable
fun QuizOption(
    letter: String,
    text: String,
    state: QuizOptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // ── Animated colors ───────────────────────────────────────────────────────
    val animSpec = tween<androidx.compose.ui.graphics.Color>(durationMillis = 220)

    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            QuizOptionState.DEFAULT -> BgElevated
            QuizOptionState.CORRECT -> CorrectBg
            QuizOptionState.WRONG   -> WrongBg
        },
        animationSpec = animSpec,
        label = "optionBg",
    )

    val borderColor by animateColorAsState(
        targetValue = when (state) {
            QuizOptionState.DEFAULT -> BorderSubtle
            QuizOptionState.CORRECT -> Correct
            QuizOptionState.WRONG   -> Wrong
        },
        animationSpec = animSpec,
        label = "optionBorder",
    )

    val circleBgColor by animateColorAsState(
        targetValue = when (state) {
            QuizOptionState.DEFAULT -> BgDeep.copy(alpha = 0.5f)
            QuizOptionState.CORRECT -> Correct
            QuizOptionState.WRONG   -> Wrong
        },
        animationSpec = animSpec,
        label = "circleBg",
    )

    val circleTextColor by animateColorAsState(
        targetValue = when (state) {
            QuizOptionState.DEFAULT -> TextSecondary
            QuizOptionState.CORRECT -> BgDeep
            QuizOptionState.WRONG   -> BgDeep
        },
        animationSpec = animSpec,
        label = "circleText",
    )

    val textColor by animateColorAsState(
        targetValue = when (state) {
            QuizOptionState.DEFAULT -> TextPrimary
            QuizOptionState.CORRECT -> Correct
            QuizOptionState.WRONG   -> Wrong
        },
        animationSpec = animSpec,
        label = "optionText",
    )

    // ── Layout ────────────────────────────────────────────────────────────────
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(OptionShape)
            .background(backgroundColor)
            .border(width = 1.dp, color = borderColor, shape = OptionShape)
            .clickable(
                enabled = state == QuizOptionState.DEFAULT,
                onClick = onClick,
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        // Letter badge circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(circleBgColor),
        ) {
            Text(
                text  = letter,
                style = StellaeTypography.bodySm.copy(
                    fontWeight = FontWeight.Bold,
                    color      = circleTextColor,
                ),
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text     = text,
            style    = StellaeTypography.bodyMd.copy(color = textColor),
            modifier = Modifier.weight(1f),
        )
    }
}
