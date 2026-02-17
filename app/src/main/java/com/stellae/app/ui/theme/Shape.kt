package com.stellae.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

// ── StellaeShapes ─────────────────────────────────────────────────────────────

object StellaeShapes {

    /** 8dp rounded corners — chips, small tags */
    val sm = RoundedCornerShape(8.dp)

    /** 12dp rounded corners — buttons, input fields */
    val md = RoundedCornerShape(12.dp)

    /** 16dp rounded corners — cards, panels */
    val lg = RoundedCornerShape(16.dp)

    /** 24dp rounded corners — bottom sheets, large modals */
    val xl = RoundedCornerShape(24.dp)

    /** Fully pill-shaped — badges, progress bars, avatar frames */
    val full = RoundedCornerShape(percent = 50)
}
