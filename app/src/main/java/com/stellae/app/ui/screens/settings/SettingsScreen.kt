package com.stellae.app.ui.screens.settings

import android.app.TimePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stellae.app.ui.components.StarfieldBackground
import com.stellae.app.ui.theme.BgCard
import com.stellae.app.ui.theme.BgDeep
import com.stellae.app.ui.theme.BgElevated
import com.stellae.app.ui.theme.BgSurface
import com.stellae.app.ui.theme.BorderGlow
import com.stellae.app.ui.theme.BorderSubtle
import com.stellae.app.ui.theme.Gold
import com.stellae.app.ui.theme.GoldDim
import com.stellae.app.ui.theme.StellaeShapes
import com.stellae.app.ui.theme.StellaeTypography
import com.stellae.app.ui.theme.TextMuted
import com.stellae.app.ui.theme.TextPrimary
import com.stellae.app.ui.theme.TextSecondary

// ── SettingsScreen ────────────────────────────────────────────────────────────

/**
 * App settings panel for Stellae.
 *
 * Usage:
 *   SettingsScreen(onBack = { navController.popBackStack() })
 */
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // ── Preferences state ─────────────────────────────────────────────────────
    var decanSystem by rememberSaveable { mutableStateOf("ptolemaic") }  // "ptolemaic" | "chaldean"
    var sessionLength by rememberSaveable { mutableStateOf("medium") }   // "short" | "medium" | "long"
    var reminderEnabled by rememberSaveable { mutableStateOf(false) }
    var reminderHour by rememberSaveable { mutableIntStateOf(9) }
    var reminderMinute by rememberSaveable { mutableIntStateOf(0) }
    var reducedMotion by rememberSaveable { mutableStateOf(false) }
    var fontSize by rememberSaveable { mutableStateOf("normal") }        // "normal" | "large" | "xlarge"
    var weeklyGoal by rememberSaveable { mutableIntStateOf(5) }          // 3, 5, or 7

    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BgDeep)
    ) {
        StarfieldBackground(starCount = 50)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar
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
                    text = "Settings",
                    style = StellaeTypography.displaySm.copy(color = Gold)
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ── Study System ──────────────────────────────────────────────
                SettingsSection(title = "Study System") {
                    SegmentedPicker(
                        label = "Decan System",
                        description = "The tradition used for decan (face) rulerships",
                        options = listOf("Ptolemaic" to "ptolemaic", "Chaldean" to "chaldean"),
                        selectedValue = decanSystem,
                        onSelect = { decanSystem = it }
                    )
                    HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                    SegmentedPicker(
                        label = "Session Length Target",
                        description = "Approximate length of each study session",
                        options = listOf(
                            "Short (5m)" to "short",
                            "Medium (10m)" to "medium",
                            "Long (15m)" to "long"
                        ),
                        selectedValue = sessionLength,
                        onSelect = { sessionLength = it }
                    )
                }

                // ── Notifications ─────────────────────────────────────────────
                SettingsSection(title = "Notifications") {
                    ToggleRow(
                        label = "Daily Reminder",
                        description = "Receive a reminder to study each day",
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it }
                    )
                    if (reminderEnabled) {
                        HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                        TimePickerRow(
                            hour = reminderHour,
                            minute = reminderMinute,
                            onTimePick = { h, m ->
                                reminderHour = h
                                reminderMinute = m
                            }
                        )
                    }
                }

                // ── Accessibility ─────────────────────────────────────────────
                SettingsSection(title = "Accessibility") {
                    ToggleRow(
                        label = "Reduced Motion",
                        description = "Minimise animations throughout the app",
                        checked = reducedMotion,
                        onCheckedChange = { reducedMotion = it }
                    )
                    HorizontalDivider(color = BorderSubtle, thickness = 0.5.dp)
                    SegmentedPicker(
                        label = "Font Size",
                        description = "Adjust text size across the app",
                        options = listOf(
                            "Normal" to "normal",
                            "Large" to "large",
                            "X-Large" to "xlarge"
                        ),
                        selectedValue = fontSize,
                        onSelect = { fontSize = it }
                    )
                }

                // ── Goals ─────────────────────────────────────────────────────
                SettingsSection(title = "Goals") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Weekly Goal",
                            style = StellaeTypography.bodyMd.copy(
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        )
                        Text(
                            text = "Number of days per week to study",
                            style = StellaeTypography.bodySm.copy(color = TextSecondary),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(3, 5, 7).forEach { goal ->
                                val isSelected = goal == weeklyGoal
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(StellaeShapes.md)
                                        .background(if (isSelected) Gold.copy(alpha = 0.15f) else BgElevated)
                                        .border(
                                            1.dp,
                                            if (isSelected) Gold else BorderSubtle,
                                            StellaeShapes.md
                                        )
                                        .clickable { weeklyGoal = goal }
                                        .padding(vertical = 14.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = goal.toString(),
                                            style = StellaeTypography.displaySm.copy(
                                                color = if (isSelected) Gold else TextPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        Text(
                                            text = "days",
                                            style = StellaeTypography.caption.copy(color = TextMuted)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── About ─────────────────────────────────────────────────────
                SettingsSection(title = "About") {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = GoldDim,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(androidx.compose.foundation.layout.Modifier.size(10.dp))
                            Text(
                                text = "Stellae",
                                style = StellaeTypography.bodyMd.copy(
                                    color = Gold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        AboutRow(label = "Version", value = "1.0.0")
                        HorizontalDivider(
                            color = BorderSubtle,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        AboutRow(label = "Build", value = "2026.02.17")
                        HorizontalDivider(
                            color = BorderSubtle,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        AboutRow(label = "Dignity System", value = "Egyptian Terms / Ptolemaic")
                        HorizontalDivider(
                            color = BorderSubtle,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                        AboutRow(label = "Lot Tradition", value = "Hellenistic (Paulus / Valens)")

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Essential dignity data sourced from classical Hellenistic and Ptolemaic traditions. Stellae is a study companion — always verify with primary sources.",
                            style = StellaeTypography.bodySm.copy(color = TextMuted)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ── Section wrapper ───────────────────────────────────────────────────────────

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(StellaeShapes.lg)
            .background(BgCard)
            .border(1.dp, BorderSubtle, StellaeShapes.lg)
    ) {
        Text(
            text = title.uppercase(),
            style = StellaeTypography.label.copy(color = TextMuted),
            modifier = Modifier
                .fillMaxWidth()
                .background(BgSurface)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
        content()
    }
}

// ── Segmented picker ──────────────────────────────────────────────────────────

@Composable
private fun SegmentedPicker(
    label: String,
    description: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onSelect: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Text(
            text = label,
            style = StellaeTypography.bodyMd.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        )
        Text(
            text = description,
            style = StellaeTypography.bodySm.copy(color = TextSecondary),
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(StellaeShapes.md)
                .background(BgElevated)
                .border(1.dp, BorderSubtle, StellaeShapes.md)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEach { (displayName, value) ->
                val isSelected = value == selectedValue
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(StellaeShapes.sm)
                        .background(if (isSelected) Gold else Color.Transparent)
                        .clickable { onSelect(value) }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = displayName,
                        style = StellaeTypography.bodySm.copy(
                            color = if (isSelected) BgDeep else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

// ── Toggle row ────────────────────────────────────────────────────────────────

@Composable
private fun ToggleRow(
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = StellaeTypography.bodyMd.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = description,
                style = StellaeTypography.bodySm.copy(color = TextSecondary)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BgDeep,
                checkedTrackColor = Gold,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = BgElevated
            )
        )
    }
}

// ── Time picker row ───────────────────────────────────────────────────────────

@Composable
private fun TimePickerRow(
    hour: Int,
    minute: Int,
    onTimePick: (Int, Int) -> Unit
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = GoldDim,
            modifier = Modifier.size(18.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        ) {
            Text(
                text = "Reminder Time",
                style = StellaeTypography.bodyMd.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Box(
            modifier = Modifier
                .clip(StellaeShapes.sm)
                .background(BgElevated)
                .border(1.dp, GoldDim, StellaeShapes.sm)
                .clickable {
                    TimePickerDialog(
                        context,
                        { _, h, m -> onTimePick(h, m) },
                        hour,
                        minute,
                        true
                    ).show()
                }
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(
                text = "%02d:%02d".format(hour, minute),
                style = StellaeTypography.bodyMd.copy(
                    color = Gold,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

// ── About row ─────────────────────────────────────────────────────────────────

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = StellaeTypography.bodyMd.copy(color = TextSecondary)
        )
        Text(
            text = value,
            style = StellaeTypography.bodyMd.copy(
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

private val BgSurface = Color(0xFF232A45)
