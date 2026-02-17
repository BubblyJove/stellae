package com.stellae.app.domain.gamification

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

/**
 * Evaluates and updates the user's daily practice streak.
 *
 * Rules:
 * - If the last practice date was **yesterday**, the streak increments by 1.
 * - If the last practice date was **today**, the streak stays the same
 *   (already practiced today; calling again should not double-count).
 * - If the last practice date was **anything earlier** (or the string is blank /
 *   unparseable), the streak resets to 1.
 *
 * Milestones fire on the exact day the streak count reaches a threshold and
 * on every multiple of that threshold thereafter (7, 14, 21 … for WEEK, etc.).
 */
class StreakManager @Inject constructor() {

    data class StreakResult(
        val newCount: Int,
        val maintained: Boolean,
        val isMilestone: Boolean,
        val milestoneType: MilestoneType?
    )

    enum class MilestoneType(val days: Int, val label: String) {
        WEEK(7, "One Week!"),
        MONTH(30, "One Month!"),
        CENTURY(100, "Century!"),
        YEAR(365, "Full Year!")
    }

    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /**
     * Evaluate the streak given [currentCount] and [lastDateStr].
     *
     * @param currentCount Stored streak count before this call.
     * @param lastDateStr  ISO-8601 date string of the last practice day (e.g. "2025-06-01").
     *                     An empty string is treated as no prior practice.
     * @param today        The current date; defaults to [LocalDate.now()].
     * @return [StreakResult] with the updated count, whether it was maintained,
     *         and any milestone that was reached.
     */
    fun checkStreak(
        currentCount: Int,
        lastDateStr: String,
        today: LocalDate = LocalDate.now()
    ): StreakResult {
        val lastDate: LocalDate? = parseDate(lastDateStr)

        val newCount: Int
        val maintained: Boolean

        when {
            lastDate == null -> {
                // No prior practice or unreadable date — start fresh
                newCount = 1
                maintained = false
            }
            lastDate == today -> {
                // Already practiced today; keep the count as-is
                newCount = currentCount
                maintained = true
            }
            lastDate == today.minusDays(1) -> {
                // Practiced yesterday — extend the streak
                newCount = currentCount + 1
                maintained = true
            }
            else -> {
                // Gap of 2+ days — streak broken, reset to 1
                newCount = 1
                maintained = false
            }
        }

        val milestone = getMilestone(newCount)

        return StreakResult(
            newCount = newCount,
            maintained = maintained,
            isMilestone = milestone != null,
            milestoneType = milestone
        )
    }

    /**
     * Return the highest-priority [MilestoneType] that [count] exactly hits
     * (on or after reaching the threshold, checking multiples).
     *
     * Priority order (highest first): YEAR, CENTURY, MONTH, WEEK.
     */
    fun getMilestone(count: Int): MilestoneType? {
        if (count <= 0) return null
        // Check from highest threshold down so a 365-day streak reports YEAR, not WEEK
        return MilestoneType.entries
            .sortedByDescending { it.days }
            .firstOrNull { count >= it.days && count % it.days == 0 }
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private fun parseDate(dateStr: String): LocalDate? {
        if (dateStr.isBlank()) return null
        return try {
            LocalDate.parse(dateStr, dateFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
