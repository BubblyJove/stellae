package com.stellae.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.stellae.app.ui.theme.TextPrimary

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
    object Boss          : Screen("boss")
    object Achievements  : Screen("achievements")
    object Reference     : Screen("reference")
    object LotCalculator : Screen("lot_calculator")
    object Progress      : Screen("progress")
    object SpeedArena    : Screen("speed_arena")
    object Settings      : Screen("settings")
    object Onboarding    : Screen("onboarding")
}

// ── Placeholder screen helper ─────────────────────────────────────────────────

@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier.fillMaxSize(),
    ) {
        Text(
            text  = name,
            color = TextPrimary,
        )
    }
}

// ── StellaeNavHost ────────────────────────────────────────────────────────────

/**
 * Root navigation host for the Stellae app.
 *
 * Placeholder composables are wired in for every destination; swap them out
 * for real screen implementations as they are built.
 *
 * Usage:
 *   StellaeTheme {
 *       StellaeNavHost()
 *   }
 *
 * To navigate programmatically supply your own [navController]:
 *   val nav = rememberNavController()
 *   StellaeNavHost(navController = nav)
 *   // later: nav.navigate(Screen.Quiz.route)
 */
@Composable
fun StellaeNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route,
) {
    NavHost(
        navController    = navController,
        startDestination = startDestination,
    ) {

        composable(Screen.Home.route) {
            PlaceholderScreen("Home")
        }

        composable(Screen.Quiz.route) {
            PlaceholderScreen("Quiz")
        }

        composable(Screen.Summary.route) {
            PlaceholderScreen("Summary")
        }

        composable(Screen.Wheel.route) {
            PlaceholderScreen("Wheel")
        }

        composable(Screen.Boss.route) {
            PlaceholderScreen("Boss")
        }

        composable(Screen.Achievements.route) {
            PlaceholderScreen("Achievements")
        }

        composable(Screen.Reference.route) {
            PlaceholderScreen("Reference")
        }

        composable(Screen.LotCalculator.route) {
            PlaceholderScreen("Lot Calculator")
        }

        composable(Screen.Progress.route) {
            PlaceholderScreen("Progress")
        }

        composable(Screen.SpeedArena.route) {
            PlaceholderScreen("Speed Arena")
        }

        composable(Screen.Settings.route) {
            PlaceholderScreen("Settings")
        }

        composable(Screen.Onboarding.route) {
            PlaceholderScreen("Onboarding")
        }
    }
}
