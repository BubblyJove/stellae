package com.stellae.app.ui.screens.wheel

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stellae.app.domain.model.DignityType
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

// ── Ring metadata ─────────────────────────────────────────────────────────────

private data class RingMeta(
    val key: String,
    val label: String,
    val color: Color
)

private val RING_META = listOf(
    RingMeta("zodiac",      "Zodiac Signs",  Color(0xFFD45A30)),
    RingMeta("domicile",    "Domiciles",     Color(0xFFD4A832)),
    RingMeta("exaltation",  "Exaltations",   Color(0xFFB8C4D8)),
    RingMeta("triplicity",  "Triplicities",  Color(0xFF4CA77A)),
    RingMeta("terms",       "Terms",         Color(0xFFC99A45)),
    RingMeta("decans",      "Decans",        Color(0xFF4A6FA5))
)

// ── DignityWheelScreen ────────────────────────────────────────────────────────

/**
 * Full-screen interactive Wheel of Dignity with ring-layer toggles and
 * tap-to-inspect segment tooltips.
 *
 * Usage:
 *   DignityWheelScreen(onBack = { navController.popBackStack() })
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DignityWheelScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var wheelState by remember { mutableStateOf(WheelState()) }
    var selectedSegment by remember { mutableStateOf<SegmentInfo?>(null) }
    var showLayersSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        // Animated starfield background
        StarfieldBackground(starCount = 80)

        // ── Wheel canvas ──────────────────────────────────────────────────────
        WheelCanvas(
            wheelState = wheelState,
            onSegmentTapped = { info -> selectedSegment = info },
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 72.dp, bottom = 80.dp)
        )

        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Gold
                )
            }

            Text(
                text = "Wheel of Dignity",
                style = StellaeTypography.displaySm.copy(color = Gold),
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )

            // Layers toggle button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(BgElevated)
                    .border(1.dp, GoldDim, CircleShape)
                    .clickable {
                        scope.launch {
                            showLayersSheet = true
                            sheetState.show()
                        }
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = "Toggle rings",
                    tint = Gold,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
        }

        // ── Segment info tooltip ──────────────────────────────────────────────
        selectedSegment?.let { info ->
            SegmentTooltip(
                info = info,
                onDismiss = { selectedSegment = null },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }

    // ── Ring toggle bottom sheet ──────────────────────────────────────────────
    if (showLayersSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                showLayersSheet = false
            },
            sheetState = sheetState,
            containerColor = BgCard,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp)
            ) {
                Text(
                    text = "Ring Layers",
                    style = StellaeTypography.displaySm.copy(color = Gold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                RING_META.forEach { ring ->
                    val isVisible = ring.key in wheelState.visibleRings
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val updated = wheelState.visibleRings.toMutableSet()
                                if (isVisible) updated.remove(ring.key) else updated.add(ring.key)
                                wheelState = wheelState.copy(visibleRings = updated)
                            }
                            .padding(vertical = 10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(ring.color)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = ring.label,
                            style = StellaeTypography.bodyMd.copy(color = TextPrimary),
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = isVisible,
                            onCheckedChange = { checked ->
                                val updated = wheelState.visibleRings.toMutableSet()
                                if (checked) updated.add(ring.key) else updated.remove(ring.key)
                                wheelState = wheelState.copy(visibleRings = updated)
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Gold,
                                uncheckedColor = TextMuted,
                                checkmarkColor = BgDeep
                            )
                        )
                    }
                }
            }
        }
    }
}

// ── SegmentTooltip ────────────────────────────────────────────────────────────

@Composable
private fun SegmentTooltip(
    info: SegmentInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dignityColor = when (info.dignityType) {
        DignityType.DOMICILE   -> Color(0xFFD4A832)
        DignityType.EXALTATION -> Color(0xFFB8C4D8)
        DignityType.TRIPLICITY -> Color(0xFFD45A30)
        DignityType.TERM       -> Color(0xFF4CA77A)
        DignityType.DECAN      -> Color(0xFFC99A45)
        else -> Gold
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderGlow, StellaeShapes.lg)
            .clickable(onClick = onDismiss)
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Ring name chip
                Box(
                    modifier = Modifier
                        .clip(StellaeShapes.sm)
                        .background(dignityColor.copy(alpha = 0.18f))
                        .border(
                            1.dp,
                            dignityColor.copy(alpha = 0.50f),
                            StellaeShapes.sm
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = info.ringName,
                        style = StellaeTypography.label.copy(color = dignityColor)
                    )
                }

                Text(
                    text = "Tap to dismiss",
                    style = StellaeTypography.caption.copy(color = TextMuted)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sign glyph or planet glyph
                val displayGlyph = info.planetGlyph ?: ""
                if (displayGlyph.isNotEmpty()) {
                    Text(
                        text = displayGlyph,
                        style = StellaeTypography.displayMd.copy(color = dignityColor),
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }

                Column {
                    Text(
                        text = info.signName,
                        style = StellaeTypography.displaySm.copy(color = TextPrimary)
                    )
                    if (info.planetName != null) {
                        Text(
                            text = info.planetName,
                            style = StellaeTypography.bodyMd.copy(color = Gold),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    if (info.degreeRange != null) {
                        Text(
                            text = info.degreeRange,
                            style = StellaeTypography.bodySm.copy(color = TextSecondary),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
