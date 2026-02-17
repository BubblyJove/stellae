package com.stellae.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

// ── Color scheme ─────────────────────────────────────────────────────────────

private val StellaeDarkColorScheme = darkColorScheme(
    primary            = Gold,
    onPrimary          = BgDeep,
    primaryContainer   = GoldDim,
    onPrimaryContainer = TextPrimary,

    secondary            = Silver,
    onSecondary          = BgDeep,
    secondaryContainer   = BgElevated,
    onSecondaryContainer = TextSecondary,

    background    = BgDeep,
    onBackground  = TextPrimary,

    surface           = BgCard,
    onSurface         = TextPrimary,
    surfaceVariant    = BgElevated,
    onSurfaceVariant  = TextSecondary,

    error    = Wrong,
    onError  = BgDeep,

    outline      = BorderSubtle,
    outlineVariant = BorderGlow,
)

// ── Theme composable ─────────────────────────────────────────────────────────

/**
 * Root theme wrapper for the Stellae app.
 *
 * Usage:
 *   StellaeTheme {
 *       Surface { /* app content */ }
 *   }
 *
 * Access custom tokens anywhere inside via StellaeTheme.colors.gold, etc.
 */
@Composable
fun StellaeTheme(
    content: @Composable () -> Unit,
) {
    val stellaeColors = StellaeColors()

    CompositionLocalProvider(LocalStellaeColors provides stellaeColors) {
        MaterialTheme(
            colorScheme = StellaeDarkColorScheme,
            typography  = StellaeM3Typography,
            shapes      = androidx.compose.material3.Shapes(
                extraSmall = StellaeShapes.sm,
                small      = StellaeShapes.sm,
                medium     = StellaeShapes.md,
                large      = StellaeShapes.lg,
                extraLarge = StellaeShapes.xl,
            ),
            content = content,
        )
    }
}

// ── Theme accessor object ─────────────────────────────────────────────────────

/**
 * Convenience object for accessing Stellae design tokens inside a composition.
 *
 * Usage:
 *   val colors = StellaeTheme.colors
 *   Text(color = colors.gold, ...)
 */
object StellaeTheme {
    val colors: StellaeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalStellaeColors.current
}
