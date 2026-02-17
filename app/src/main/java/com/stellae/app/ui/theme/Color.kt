package com.stellae.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ── Background Scale ─────────────────────────────────────────────────────────
val BgDeep      = Color(0xFF0B0E1A)
val BgCard      = Color(0xFF111627)
val BgElevated  = Color(0xFF1A2038)
val BgSurface   = Color(0xFF232A45)

// rgba(255,255,255,0.06)
val BorderSubtle = Color(0x0FFFFFFF)
// rgba(201,167,106,0.25)
val BorderGlow   = Color(0x40C9A76A)

// ── Accent ───────────────────────────────────────────────────────────────────
val Gold        = Color(0xFFC9A76A)
val GoldBright  = Color(0xFFE8C87A)
val GoldDim     = Color(0xFF8A7245)
val Silver      = Color(0xFFC0C8D8)
val SilverBright = Color(0xFFE0E6F0)

// ── Planetary ────────────────────────────────────────────────────────────────
val Saturn      = Color(0xFF7A8899)
val Jupiter     = Color(0xFF4A6FA5)
val Mars        = Color(0xFFC44545)
val Sun         = Color(0xFFD4A832)
val Venus       = Color(0xFF4CA77A)
val Mercury     = Color(0xFFC99A45)
val Moon        = Color(0xFFB8C4D8)

// ── Element ──────────────────────────────────────────────────────────────────
val Fire        = Color(0xFFD45A30)
val Earth       = Color(0xFF6B8C42)
val Air         = Color(0xFF5A8EC9)
val Water       = Color(0xFF3A7AAA)

// ── Dignity Tier ─────────────────────────────────────────────────────────────
val DomicileColor    = Color(0xFFD4A832)
val ExaltationColor  = Color(0xFFC0C8D8)
val TriplicityColor  = Color(0xFFD45A30)
val TermColor        = Color(0xFF4CA77A)
val DecanColor       = Color(0xFFC99A45)

// ── Text ─────────────────────────────────────────────────────────────────────
val TextPrimary   = Color(0xFFE8E4DC)
val TextSecondary = Color(0xFF9AA0B4)
val TextMuted     = Color(0xFF5A6078)
val TextGold      = Color(0xFFC9A76A)

// ── Feedback ─────────────────────────────────────────────────────────────────
val Correct   = Color(0xFF4ADE80)
// rgba(74,222,128,0.1)
val CorrectBg = Color(0x1A4ADE80)
val Wrong     = Color(0xFFF87171)
// rgba(248,113,113,0.1)
val WrongBg   = Color(0x1AF87171)
val Streak    = Color(0xFFFBBF24)

// ── StellaeColors data class ─────────────────────────────────────────────────
data class StellaeColors(
    // Background
    val bgDeep: Color       = BgDeep,
    val bgCard: Color       = BgCard,
    val bgElevated: Color   = BgElevated,
    val bgSurface: Color    = BgSurface,
    val borderSubtle: Color = BorderSubtle,
    val borderGlow: Color   = BorderGlow,

    // Accent
    val gold: Color         = Gold,
    val goldBright: Color   = GoldBright,
    val goldDim: Color      = GoldDim,
    val silver: Color       = Silver,
    val silverBright: Color = SilverBright,

    // Planetary
    val saturn: Color   = Saturn,
    val jupiter: Color  = Jupiter,
    val mars: Color     = Mars,
    val sun: Color      = Sun,
    val venus: Color    = Venus,
    val mercury: Color  = Mercury,
    val moon: Color     = Moon,

    // Element
    val fire: Color     = Fire,
    val earth: Color    = Earth,
    val air: Color      = Air,
    val water: Color    = Water,

    // Dignity Tier
    val domicileColor:   Color = DomicileColor,
    val exaltationColor: Color = ExaltationColor,
    val triplicityColor: Color = TriplicityColor,
    val termColor:       Color = TermColor,
    val decanColor:      Color = DecanColor,

    // Text
    val textPrimary:   Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted:     Color = TextMuted,
    val textGold:      Color = TextGold,

    // Feedback
    val correct:   Color = Correct,
    val correctBg: Color = CorrectBg,
    val wrong:     Color = Wrong,
    val wrongBg:   Color = WrongBg,
    val streak:    Color = Streak,
)

val LocalStellaeColors = staticCompositionLocalOf { StellaeColors() }
