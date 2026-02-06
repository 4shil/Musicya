package com.fourshil.musicya.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Playback statistics tracker for smart playlists and recommendations.
 */
object PlaybackStats {

    private const val TAG = "PlaybackStats"
    private const val PREFS_NAME = "playback_stats"
    private const val KEY_TOTAL_SONGS_PLAYED = "total_songs_played"
    private const val KEY_TOTAL_PLAY_TIME_MS = "total_play_time_ms"
    private const val KEY_LAST_PLAYED_PREFIX = "last_played_"
    private const val KEY_PLAY_COUNT_PREFIX = "play_count_"
    private const val KEY_SKIP_COUNT_PREFIX = "skip_count_"

    private lateinit var prefs: SharedPreferences
    private var initialized = false

    private val _totalSongsPlayed = MutableStateFlow(0)
    val totalSongsPlayed: StateFlow<Int> = _totalSongsPlayed.asStateFlow()

    private val _totalPlayTimeMs = MutableStateFlow(0L)
    val totalPlayTimeMs: StateFlow<Long> = _totalPlayTimeMs.asStateFlow()

    fun init(context: Context) {
        if (initialized) return
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _totalSongsPlayed.value = prefs.getInt(KEY_TOTAL_SONGS_PLAYED, 0)
        _totalPlayTimeMs.value = prefs.getLong(KEY_TOTAL_PLAY_TIME_MS, 0)
        initialized = true
        Log.d(TAG, "PlaybackStats initialized")
    }

    /**
     * Record that a song was played.
     */
    fun recordPlay(songId: Long, playTimeMs: Long) {
        if (!initialized) return
        prefs.edit().apply {
            putInt(KEY_TOTAL_SONGS_PLAYED, _totalSongsPlayed.value + 1)
            putLong(KEY_TOTAL_PLAY_TIME_MS, _totalPlayTimeMs.value + playTimeMs)
            putLong(KEY_LAST_PLAYED_PREFIX + songId, System.currentTimeMillis())
            putInt(KEY_PLAY_COUNT_PREFIX + songId, getPlayCount(songId) + 1)
            apply()
        }
        _totalSongsPlayed.value++
        _totalPlayTimeMs.value += playTimeMs
    }

    /**
     * Record that a song was skipped.
     */
    fun recordSkip(songId: Long) {
        if (!initialized) return
        prefs.edit().apply {
            putInt(KEY_SKIP_COUNT_PREFIX + songId, getSkipCount(songId) + 1)
            apply()
        }
    }

    /**
     * Get play count for a song.
     */
    fun getPlayCount(songId: Long): Int {
        if (!initialized) return 0
        return prefs.getInt(KEY_PLAY_COUNT_PREFIX + songId, 0)
    }

    /**
     * Get skip count for a song.
     */
    fun getSkipCount(songId: Long): Int {
        if (!initialized) return 0
        return prefs.getInt(KEY_SKIP_COUNT_PREFIX + songId, 0)
    }

    /**
     * Get last played timestamp for a song.
     */
    fun getLastPlayed(songId: Long): Long {
        if (!initialized) return 0
        return prefs.getLong(KEY_LAST_PLAYED_PREFIX + songId, 0)
    }

    /**
     * Get play ratio (plays vs skips) for sorting.
     */
    fun getPlayRatio(songId: Long): Float {
        val plays = getPlayCount(songId)
        val skips = getSkipCount(songId)
        return if (skips == 0) plays.toFloat() else plays.toFloat() / (plays + skips)
    }

    /**
     * Get recently played songs (last N played).
     */
    fun getRecentlyPlayed(limit: Int = 20): List<Pair<Long, Long>> {
        if (!initialized) return emptyList()
        val allEntries = prefs.all
            .filter { it.key.startsWith(KEY_LAST_PLAYED_PREFIX) }
            .mapNotNull { (key, value) ->
                val songId = key.removePrefix(KEY_LAST_PLAYED_PREFIX).toLongOrNull()
                val timestamp = value as? Long
                if (songId != null && timestamp != null) songId to timestamp else null
            }
            .sortedByDescending { it.second }
        return allEntries.take(limit)
    }

    /**
     * Get most played songs.
     */
    fun getMostPlayed(limit: Int = 20): List<Pair<Long, Int>> {
        if (!initialized) return emptyList()
        val allEntries = prefs.all
            .filter { it.key.startsWith(KEY_PLAY_COUNT_PREFIX) }
            .mapNotNull { (key, value) ->
                val songId = key.removePrefix(KEY_PLAY_COUNT_PREFIX).toLongOrNull()
                val count = value as? Int
                if (songId != null && count != null) songId to count else null
            }
            .sortedByDescending { it.second }
        return allEntries.take(limit)
    }

    /**
     * Get total play time formatted.
     */
    fun getFormattedPlayTime(): String {
        val totalMs = _totalPlayTimeMs.value
        val hours = totalMs / (1000 * 60 * 60)
        val minutes = (totalMs / (1000 * 60)) % 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }

    /**
     * Clear all statistics.
     */
    fun clear() {
        if (!initialized) return
        prefs.edit().clear().apply()
        _totalSongsPlayed.value = 0
        _totalPlayTimeMs.value = 0
    }
}