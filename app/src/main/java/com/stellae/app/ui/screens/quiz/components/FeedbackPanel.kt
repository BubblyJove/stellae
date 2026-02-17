package com.stellae.app.ui.screens.quiz.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stellae.app.ui.theme.Correct
import com.stellae.app.ui.theme.CorrectBg
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary
import com.stellae.app.ui.theme.Wrong
import com.stellae.app.ui.theme.WrongBg

/**
 * Feedback panel displayed after the user selects an answer.
 *
 * Correct answer — green-tinted card:
 *   Header: "Correct! +X XP" in green
 *   Body:   explanation text
 *
 * Wrong answer — red-tinted card:
 *   Header: "Not yet mastered" in red
 *   Body:   explanation text
 *
 * Usage:
 *   FeedbackPanel(
 *       isCorrect   = true,
 *       xpEarned    = 14L,
 *       explanation = "The Sun rules Leo by domicile.",
 *   )
 */
@Composable
fun FeedbackPanel(
    isCorrect: Boolean,
    xpEarned: Long,
    explanation: String,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isCorrect) CorrectBg else WrongBg
    val borderColor     = if (isCorrect) Correct   else Wrong
    val accentColor     = if (isCorrect) Correct   else Wrong

    val headerText = if (isCorrect) {
        "Correct! +$xpEarned XP"
    } else {
        "Not yet mastered"
    }

    Column(
        modifier = modifier
            .clip(StellaeShapes.lg)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = StellaeShapes.lg,
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        // Header line — coloured accent text
        Text(
            text  = headerText,
            style = StellaeTypography.bodyMd.copy(
                color      = accentColor,
                fontWeight = FontWeight.Bold,
            ),
        )

        if (explanation.isNotBlank()) {
            Spacer(Modifier.height(6.dp))

            // Explanation body — standard secondary text
            Text(
                text  = explanation,
                style = StellaeTypography.bodyMd.copy(color = TextSecondary),
            )
        }
    }
}
