package com.stellae.app.domain.fsrs

/**
 * Tracks wrong-answer selections and surfaces confusion patterns.
 *
 * A "confusion pair" is an (correctAnswer, chosenAnswer) tuple. When the same
 * pair occurs [minCount] or more times it is reported as a known confusion and
 * triggers targeted drill suggestions.
 */
class ConfusionDetector {

    data class ConfusionRecord(
        val correctCardId: Long,
        val chosenAnswer: String,
        val correctAnswer: String,
        val timestamp: Long
    )

    private val recentRecords = mutableListOf<ConfusionRecord>()

    // ------------------------------------------------------------------
    // Public API
    // ------------------------------------------------------------------

    /** Append a new confusion event to the in-memory log. */
    fun recordConfusion(record: ConfusionRecord) {
        recentRecords.add(record)
    }

    /**
     * Return all (correctAnswer, chosenAnswer) pairs that appear at least
     * [minCount] times. Each pair is normalised so that the correct answer
     * always occupies the first position.
     */
    fun getConfusionPatterns(minCount: Int = 3): List<Pair<String, String>> {
        return recentRecords
            .groupBy { Pair(it.correctAnswer, it.chosenAnswer) }
            .filter { (_, records) -> records.size >= minCount }
            .keys
            .toList()
    }

    /**
     * Return `true` when [answer1] and [answer2] form a documented confusion
     * pair (in either direction) with at least 2 occurrences.
     */
    fun isKnownConfusion(answer1: String, answer2: String): Boolean {
        val forward = recentRecords.count {
            it.correctAnswer == answer1 && it.chosenAnswer == answer2
        }
        val backward = recentRecords.count {
            it.correctAnswer == answer2 && it.chosenAnswer == answer1
        }
        return (forward + backward) >= 2
    }

    /**
     * Build a list of human-readable drill suggestions derived from the
     * confusion log. Each suggestion names the two answers being mixed up
     * and how many times the error has occurred.
     *
     * Only pairs with 2+ confusions are included; results are sorted by
     * frequency (most confused first) so the learner tackles their worst
     * weak spots first.
     */
    fun getDrillSuggestions(): List<String> {
        // Aggregate counts for both directions of each pair
        data class PairKey(val a: String, val b: String)

        val counts = mutableMapOf<PairKey, Int>()

        for (record in recentRecords) {
            // Normalise order so (A,B) and (B,A) map to the same key
            val key = if (record.correctAnswer <= record.chosenAnswer) {
                PairKey(record.correctAnswer, record.chosenAnswer)
            } else {
                PairKey(record.chosenAnswer, record.correctAnswer)
            }
            counts[key] = (counts[key] ?: 0) + 1
        }

        return counts
            .filter { (_, count) -> count >= 2 }
            .entries
            .sortedByDescending { it.value }
            .map { (key, count) ->
                "Drill: \"${key.a}\" vs \"${key.b}\" (confused $count time${if (count == 1) "" else "s"})"
            }
    }

    /** Remove all records older than [cutoffMillis] epoch time. */
    fun pruneOldRecords(cutoffMillis: Long) {
        recentRecords.removeAll { it.timestamp < cutoffMillis }
    }

    /** Total number of confusion events on record. */
    fun recordCount(): Int = recentRecords.size
}
