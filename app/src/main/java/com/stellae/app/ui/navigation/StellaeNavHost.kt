package com.stellae.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.stellae.app.domain.model.SessionResult
import com.stellae.app.ui.screens.achievements.AchievementsScreen
import com.stellae.app.ui.screens.boss.BossScreen
import com.stellae.app.ui.screens.home.HomeScreen
import com.stellae.app.ui.screens.lots.LotCalculatorScreen
import com.stellae.app.ui.screens.onboarding.OnboardingScreen
import com.stellae.app.ui.screens.progress.ProgressDashboardScreen
import com.stellae.app.ui.screens.quiz.QuizScreen
import com.stellae.app.ui.screens.reference.ReferenceLibraryScreen
import com.stellae.app.ui.screens.settings.SettingsScreen
import com.stellae.app.ui.screens.speed.SpeedArenaScreen
import com.stellae.app.ui.screens.summary.SessionSummaryScreen
import com.stellae.app.ui.screens.wheel.DignityWheelScreen

// ── Screen sealed class ───────────────────────────────────────────────────────

/**
 * Typed route definitions for the Stellae navigation graph.
 *
 * Each Screen object carries a [route] string used by NavHost.
 */
sealed class Screen(val route: String) {
    object Home          : Screen("home")
    object Quiz          : Screen("quiz")
    object Summary       : Screen("summary")
    object Wheel         : Screen("wheel")
    object Boss          : Screen("boss/{bossId}") {
        fun createRoute(bossId: String) = "boss/$bossId"
    }
    object Achievements  : Screen("achievements")
    object Reference     : Screen("reference")
    object LotCalculator : Screen("lot_calculator")
    object Progress      : Screen("progress")
    object SpeedArena    : Screen("speed_arena")
    object Settings      : Screen("settings")
    object Onboarding    : Screen("onboarding")
}

// ── StellaeNavHost ────────────────────────────────────────────────────────────
@Composable
fun StellaeNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route,
) {
    // Mutable holder for the most recent session result, shared between the
    // Quiz destination's completion callback and the Summary destination's
    // screen composable.
    val sessionResultHolder = remember { SessionResultHolder() }

    NavHost(
        navController    = navController,
        startDestination = startDestination,
    ) {

        // ── Home ─────────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onStartSession = {
                    navController.navigate(Screen.Quiz.route)
                },
                onOpenWheel = {
                    navController.navigate(Screen.Wheel.route)
                },
            )
        }

        // ── Quiz ─────────────────────────────────────────────────────────────
        composable(Screen.Quiz.route) {
            QuizScreen(
                onSessionComplete = { result ->
                    sessionResultHolder.result = result
                    navController.navigate(Screen.Summary.route) {
                        // Remove the Quiz entry from the back stack so pressing
                        // back from Summary returns to Home.
                        popUpTo(Screen.Quiz.route) { inclusive = true }
                    }
                },
            )
        }

        // ── Summary ──────────────────────────────────────────────────────────
        composable(Screen.Summary.route) {
            val result = sessionResultHolder.result

            if (result != null) {
                SessionSummaryScreen(
                    sessionResult = result,
                    onDone = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    onPracticeWeakSpots = {
                        navController.navigate(Screen.Quiz.route) {
                            popUpTo(Screen.Summary.route) { inclusive = true }
                        }
                    },
                )
            } else {
                // Fallback: if the process was killed and restored without a
                // result in the holder, navigate home rather than crash.
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Summary.route) { inclusive = true }
                }
            }
        }

        // ── Wheel ────────────────────────────────────────────────────────────
        composable(Screen.Wheel.route) {
            DignityWheelScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Boss ─────────────────────────────────────────────────────────────
        composable(
            route = Screen.Boss.route,
            arguments = listOf(navArgument("bossId") { type = NavType.StringType }),
        ) { backStackEntry ->
            val bossId = backStackEntry.arguments?.getString("bossId") ?: "sol"
            BossScreen(
                bossId = bossId,
                onBack = { navController.popBackStack() },
            )
        }

        // ── Achievements ────────────────────────────────────────────────────
        composable(Screen.Achievements.route) {
            AchievementsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Reference ───────────────────────────────────────────────────────
        composable(Screen.Reference.route) {
            ReferenceLibraryScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Lot Calculator ──────────────────────────────────────────────────
        composable(Screen.LotCalculator.route) {
            LotCalculatorScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Progress ────────────────────────────────────────────────────────
        composable(Screen.Progress.route) {
            ProgressDashboardScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Speed Arena ─────────────────────────────────────────────────────
        composable(Screen.SpeedArena.route) {
            SpeedArenaScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Settings ────────────────────────────────────────────────────────
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        // ── Onboarding ──────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
            )
        }
    }
}

// ── SessionResultHolder ───────────────────────────────────────────────────────

/**
 * Simple in-process mutable container for the most recent [SessionResult].
 *
 * This avoids the complexity of Parcelable/JSON serialisation while still
 * letting the nav graph hand the result from Quiz to Summary. The holder is
 * created once with [remember] in [StellaeNavHost] and survives recomposition.
 */
private class SessionResultHolder {
    var result: SessionResult? = null
}
