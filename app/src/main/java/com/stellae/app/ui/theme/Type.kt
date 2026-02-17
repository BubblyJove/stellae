package com.stellae.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

// Intended display font: Cormorant Garamond (Google Fonts)
// Intended body font:    DM Sans (Google Fonts)
// Using system fallbacks until the google-fonts Compose dependency is added.
// To enable:
//   1. Add "androidx.compose.ui:ui-text-google-fonts:<version>" to build.gradle.
//   2. Replace the FontFamily references below with GoogleFont-backed families.

private val DisplayFontFamily = FontFamily.Serif   // -> Cormorant Garamond
private val BodyFontFamily    = FontFamily.Default  // -> DM Sans

// ── StellaeTypography ────────────────────────────────────────────────────────

object StellaeTypography {

    /** 36sp / Bold (700) / serif — large display headings */
    val displayLg = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 36.sp,
        lineHeight = 44.sp,
    )

    /** 28sp / SemiBold (600) / serif — section headings */
    val displayMd = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 28.sp,
        lineHeight = 36.sp,
    )

    /** 22sp / SemiBold (600) / serif — card titles */
    val displaySm = TextStyle(
        fontFamily = DisplayFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 22.sp,
        lineHeight = 30.sp,
    )

    /** 17sp / Normal (400) / sans — primary body copy */
    val bodyLg = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 17.sp,
        lineHeight = 26.sp,
    )

    /** 15sp / Normal (400) / sans — standard body copy */
    val bodyMd = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 15.sp,
        lineHeight = 23.sp,
    )

    /** 13sp / Normal (400) / sans — small body copy */
    val bodySm = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 13.sp,
        lineHeight = 20.sp,
    )

    /** 12sp / Medium (500) / sans — uppercase labels with wide tracking */
    val label = TextStyle(
        fontFamily    = BodyFontFamily,
        fontWeight    = FontWeight.Medium,
        fontSize      = 12.sp,
        lineHeight    = 16.sp,
        letterSpacing = 0.08.em,
    )

    /** 11sp / Normal (400) / sans — captions and fine print */
    val caption = TextStyle(
        fontFamily = BodyFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 11.sp,
        lineHeight = 16.sp,
    )
}

// ── Material3 Typography mapping ─────────────────────────────────────────────

val StellaeM3Typography = Typography(
    displayLarge  = StellaeTypography.displayLg,
    displayMedium = StellaeTypography.displayMd,
    displaySmall  = StellaeTypography.displaySm,
    headlineLarge  = StellaeTypography.displayMd,
    headlineMedium = StellaeTypography.displaySm,
    headlineSmall  = StellaeTypography.displaySm,
    bodyLarge   = StellaeTypography.bodyLg,
    bodyMedium  = StellaeTypography.bodyMd,
    bodySmall   = StellaeTypography.bodySm,
    labelLarge  = StellaeTypography.label,
    labelMedium = StellaeTypography.label,
    labelSmall  = StellaeTypography.caption,
)
