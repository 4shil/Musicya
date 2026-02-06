package com.fourshil.musicya.util

/**
 * Simple fuzzy search scoring using Levenshtein distance.
 * Returns a score from 0.0 (no match) to 1.0 (exact match).
 */
object FuzzySearch {

    /**
     * Check if query fuzzy-matches the target string.
     * Uses a combination of substring matching and Levenshtein distance.
     */
    fun matches(query: String, target: String): Boolean {
        if (query.isBlank()) return true
        val q = query.lowercase().trim()
        val t = target.lowercase().trim()

        // Exact substring match
        if (t.contains(q)) return true

        // All characters of query appear in order in target
        var qi = 0
        for (ch in t) {
            if (qi < q.length && ch == q[qi]) qi++
        }
        return qi == q.length
    }

    /**
     * Score a query against a target string.
     * Higher score = better match.
     */
    fun score(query: String, target: String): Float {
        if (query.isBlank()) return 0f
        val q = query.lowercase().trim()
        val t = target.lowercase().trim()

        // Exact match
        if (t == q) return 1f

        // Starts with query
        if (t.startsWith(q)) return 0.9f

        // Contains query as substring
        if (t.contains(q)) return 0.8f

        // All characters in order (acronym / initials match)
        var qi = 0
        for (ch in t) {
            if (qi < q.length && ch == q[qi]) qi++
        }
        if (qi == q.length) return 0.6f

        // Levenshtein-based similarity
        val dist = levenshtein(q, t)
        val maxLen = maxOf(q.length, t.length)
        if (maxLen == 0) return 0f
        val similarity = 1f - (dist.toFloat() / maxLen.toFloat())
        return if (similarity > 0.5f) similarity * 0.5f else 0f
    }

    /**
     * Levenshtein edit distance between two strings.
     */
    fun levenshtein(a: String, b: String): Int {
        val m = a.length
        val n = b.length
        if (m == 0) return n
        if (n == 0) return m

        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j

        for (i in 1..m) {
            for (j in 1..n) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,      // deletion
                    dp[i][j - 1] + 1,      // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
            }
        }
        return dp[m][n]
    }
}
