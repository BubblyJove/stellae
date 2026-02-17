package com.stellae.app.ui.screens.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.stellae.app.domain.model.SessionResult
import com.stellae.app.ui.components.PrimaryButton
import com.stellae.app.ui.components.QuizOption
import com.stellae.app.ui.components.QuizOptionState
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.components.XpProgressBar
import com.stellae.app.ui.screens.quiz.components.FeedbackPanel
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary

private val OPTION_LETTERS = listOf("A", "B", "C", "D")

/**
 * Quiz screen — presents one question at a time from the FSRS card queue.
 *
 * Usage:
 *   QuizScreen(
 *       onSessionComplete = { result -> navController.navigate(Screen.Summary.route) }
 *   )
 */
@Composable
fun QuizScreen(
    onSessionComplete: (SessionResult) -> Unit,
    viewModel: QuizViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate away the moment the session is flagged complete.
    LaunchedEffect(uiState.isSessionComplete) {
        if (uiState.isSessionComplete) {
            onSessionComplete(viewModel.getSessionResult())
        }
    }

    val statusBarPadding     = WindowInsets.statusBars.asPaddingValues()
    val navigationBarPadding = WindowInsets.navigationBars.asPaddingValues()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        StarfieldBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = statusBarPadding.calculateTopPadding())
                .padding(bottom = navigationBarPadding.calculateBottomPadding())
                .padding(horizontal = 20.dp),
        ) {

            Spacer(Modifier.height(16.dp))

            // ── Top bar: question counter + XP earned ────────────────────────
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text  = "${uiState.questionNumber} / ${uiState.totalQuestions}",
                    style = StellaeTypography.bodyMd.copy(
                        color      = TextSecondary,
                        fontWeight = FontWeight.Medium,
                    ),
                )

                Text(
                    text  = "+${uiState.xpEarned} XP",
                    style = StellaeTypography.bodyMd.copy(
                        color      = Gold,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }

            Spacer(Modifier.height(10.dp))

            // ── Session progress bar ─────────────────────────────────────────
            XpProgressBar(
                progress = uiState.sessionProgress,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))

            // ── Dignity type label ────────────────────────────────────────────
            val question = uiState.currentQuestion
            if (question != null) {
                Text(
                    text  = question.dignityTypeLabel.uppercase(),
                    style = StellaeTypography.label.copy(
                        color         = TextMuted,
                        letterSpacing = 0.12.em,
                    ),
                )

                Spacer(Modifier.height(10.dp))

                // ── Question text ─────────────────────────────────────────────
                Text(
                    text  = question.questionText,
                    style = StellaeTypography.displaySm.copy(color = TextPrimary),
                )

                Spacer(Modifier.height(28.dp))

                // ── Answer options ────────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    question.options.forEachIndexed { index, optionText ->
                        val optionState = when {
                            uiState.selectedAnswer == null             -> QuizOptionState.DEFAULT
                            index == question.correctIndex             -> QuizOptionState.CORRECT
                            index == uiState.selectedAnswer            -> QuizOptionState.WRONG
                            else                                       -> QuizOptionState.DEFAULT
                        }

                        QuizOption(
                            letter   = OPTION_LETTERS.getOrElse(index) { (index + 1).toString() },
                            text     = optionText,
                            state    = optionState,
                            onClick  = { viewModel.selectAnswer(index) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                // ── Feedback panel (shown after answering) ────────────────────
                if (uiState.selectedAnswer != null && uiState.isCorrect != null) {
                    Spacer(Modifier.height(16.dp))

                    FeedbackPanel(
                        isCorrect   = uiState.isCorrect!!,
                        xpEarned    = uiState.xpEarned,
                        explanation = uiState.feedbackExplanation,
                        modifier    = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(20.dp))

                    // ── Next button ───────────────────────────────────────────
                    PrimaryButton(
                        text     = "Next \u2192",
                        onClick  = { viewModel.nextQuestion() },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// Convenience extension so `.em` compiles without importing TextUnit directly.
private val Double.em get() = androidx.compose.ui.unit.TextUnit(
    this.toFloat(),
    androidx.compose.ui.unit.TextUnitType.Em,
)
