package com.fourshil.musicya.util

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Utility for cleaning up duplicate songs from the library.
 */
object DuplicateCleanup {

    private const val TAG = "DuplicateCleanup"

    /**
     * Find potential duplicate songs based on title and artist similarity.
     */
    fun findDuplicates(songs: List<Song>): List<DuplicateGroup> {
        val duplicates = mutableListOf<DuplicateGroup>()
        val processed = mutableSetOf<Long>()

        songs.forEach { song ->
            if (song.id in processed) return@forEach

            val similar = songs.filter { other ->
                other.id != song.id &&
                other.id !in processed &&
                isDuplicate(song, other)
            }

            if (similar.isNotEmpty()) {
                duplicates.add(DuplicateGroup(song, similar))
                processed.add(song.id)
                processed.addAll(similar.map { it.id })
            }
        }

        return duplicates
    }

    /**
     * Check if two songs are duplicates based on name and artist.
     */
    private fun isDuplicate(song1: Song, song2: Song): Boolean {
        val title1 = song1.title.lowercase().trim()
        val title2 = song2.title.lowercase().trim()
        val artist1 = song1.artist.lowercase().trim()
        val artist2 = song2.artist.lowercase().trim()

        // Exact match
        if (title1 == title2 && artist1 == artist2) return true

        // Very similar titles with same artist
        if (title1 == title2 && levenshteinDistance(artist1, artist2) <= 2) return true
        if (artist1 == artist2 && levenshteinDistance(title1, title2) <= 2) return true

        // High similarity score
        val similarity = calculateSimilarity(title1, title2)
        return similarity > 0.9f && artist1 == artist2
    }

    /**
     * Calculate Levenshtein distance between two strings.
     */
    private fun levenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }
        for (i in 0..s1.length) dp[i][0] = i
        for (j in 0..s2.length) dp[0][j] = j
        for (i in 1..s1.length) {
            for (j in 1..s2.length) {
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,
                    dp[i][j - 1] + 1,
                    dp[i - 1][j - 1] + if (s1[i - 1] == s2[j - 1]) 0 else 1
                )
            }
        }
        return dp[s1.length][s2.length]
    }

    /**
     * Calculate similarity ratio between two strings.
     */
    private fun calculateSimilarity(s1: String, s2: String): Float {
        if (s1 == s2) return 1.0f
        val distance = levenshteinDistance(s1, s2)
        val maxLen = maxOf(s1.length, s2.length)
        return 1.0f - (distance.toFloat() / maxLen)
    }

    data class DuplicateGroup(
        val primary: Song,
        val duplicates: List<Song>
    )
}

/**
 * Backup manager for creating and restoring app backups.
 */
object BackupManager {

    private const val TAG = "BackupManager"
    private const val BACKUP_PREFIX = "musicya_backup_"
    private const val VERSION = 1

    /**
     * Create a full app backup including settings, playlists, and playback history.
     */
    suspend fun createBackup(
        context: Context,
        outputDir: File
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val backupFile = File(outputDir, "${BACKUP_PREFIX}${timestamp}.json")

            val backupData = JSONObject().apply {
                put("version", VERSION)
                put("timestamp", System.currentTimeMillis())
                put("appVersion", context.packageManager.getPackageInfo(context.packageName, 0).versionName)
                put("backup", JSONObject().apply {
                    put("settings", getSettingsJson(context))
                    put("playlists", getPlaylistsJson(context))
                    put("history", getHistoryJson(context))
                    put("favorites", getFavoritesJson(context))
                })
            }

            backupFile.writeText(backupData.toString(2))
            Log.d(TAG, "Created backup: ${backupFile.absolutePath}")
            Result.success(backupFile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create backup", e)
            Result.failure(e)
        }
    }

    private fun getSettingsJson(context: Context): JSONObject {
        val prefs = context.getSharedPreferences("musicya_prefs", Context.MODE_PRIVATE)
        return JSONObject().apply {
            prefs.all.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Int -> put(key, value)
                    is Long -> put(key, value)
                    is Boolean -> put(key, value)
                }
            }
        }
    }

    private fun getPlaylistsJson(context: Context): JSONArray {
        // Would query database for playlists
        return JSONArray()
    }

    private fun getHistoryJson(context: Context): JSONArray {
        // Would query playback history
        return JSONArray()
    }

    private fun getFavoritesJson(context: Context): JSONArray {
        // Would query favorites
        return JSONArray()
    }

    /**
     * Restore app from backup file.
     */
    suspend fun restoreBackup(
        context: Context,
        backupFile: File
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = backupFile.readText()
            val backupData = JSONObject(json)
            val version = backupData.optInt("version", 1)

            if (version > VERSION) {
                return@withContext Result.failure(Exception("Backup version too new"))
            }

            val backup = backupData.getJSONObject("backup")
            restoreSettings(context, backup.getJSONObject("settings"))
            restorePlaylists(context, backup.getJSONArray("playlists"))
            restoreHistory(context, backup.getJSONArray("history"))
            restoreFavorites(context, backup.getJSONArray("favorites"))

            Log.d(TAG, "Restored backup from ${backupFile.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore backup", e)
            Result.failure(e)
        }
    }

    private fun restoreSettings(context: Context, settings: JSONObject) {
        val prefs = context.getSharedPreferences("musicya_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        settings.keys().forEach { key ->
            when (val value = settings.get(key)) {
                is String -> editor.putString(key, value)
                is Int -> editor.putInt(key, value)
                is Long -> editor.putLong(key, value)
                is Boolean -> editor.putBoolean(key, value)
            }
        }
        editor.apply()
    }

    private fun restorePlaylists(context: Context, playlists: JSONArray) {
        // Would restore playlists to database
    }

    private fun restoreHistory(context: Context, history: JSONArray) {
        // Would restore playback history
    }

    private fun restoreFavorites(context: Context, favorites: JSONArray) {
        // Would restore favorites
    }
}

private suspend fun <T> withContext(context: kotlinx.coroutines.CoroutineDispatcher, block: suspend () -> T): T {
    return kotlinx.coroutines.withContext(context, block)
}