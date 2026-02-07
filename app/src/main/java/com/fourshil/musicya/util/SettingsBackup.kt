package com.fourshil.musicya.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Backup and restore app settings to/from JSON file.
 */
object SettingsBackup {

    private const val TAG = "SettingsBackup"
    private const val BACKUP_VERSION = 1

    /**
     * Export all app settings to a JSON file.
     */
    suspend fun exportSettings(
        context: Context,
        prefs: SharedPreferences,
        uri: Uri
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val allPrefs = prefs.all
            val json = JSONObject().apply {
                put("version", BACKUP_VERSION)
                put("timestamp", System.currentTimeMillis())
                put("settings", JSONObject().apply {
                    allPrefs.forEach { (key, value) ->
                        when (value) {
                            is String -> put(key, value)
                            is Int -> put(key, value)
                            is Long -> put(key, value)
                            is Float -> put(key, value)
                            is Boolean -> put(key, value)
                            is Set<*> -> put(key, JSONArray(value))
                        }
                    }
                })
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toString(2).toByteArray())
            }
            Log.d(TAG, "Settings exported successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export settings", e)
            false
        }
    }

    /**
     * Import settings from a JSON file.
     */
    suspend fun importSettings(
        context: Context,
        prefs: SharedPreferences,
        uri: Uri
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: return@withContext false

            val json = JSONObject(jsonString)
            val version = json.optInt("version", 1)
            val settings = json.optJSONObject("settings") ?: return@withContext false

            val editor = prefs.edit()
            settings.keys().forEach { key ->
                when (val value = settings.get(key)) {
                    is String -> editor.putString(key, value)
                    is Int -> editor.putInt(key, value)
                    is Long -> editor.putLong(key, value)
                    is Float -> editor.putFloat(key, value)
                    is Boolean -> editor.putBoolean(key, value)
                    is JSONArray -> {
                        val set = mutableSetOf<String>()
                        for (i in 0 until value.length()) {
                            set.add(value.getString(i))
                        }
                        editor.putStringSet(key, set)
                    }
                }
            }
            editor.apply()
            Log.d(TAG, "Settings imported successfully (version $version)")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import settings", e)
            false
        }
    }

    /**
     * Get a summary of what will be backed up.
     */
    fun getBackupSummary(prefs: SharedPreferences): String {
        val allPrefs = prefs.all
        val categories = mutableMapOf<String, Int>()
        
        allPrefs.keys.forEach { key ->
            val category = when {
                key.startsWith("theme") || key.startsWith("dark") -> "Appearance"
                key.startsWith("audio") || key.startsWith("equalizer") || key.startsWith("crossfade") -> "Audio"
                key.startsWith("playback") || key.startsWith("speed") -> "Playback"
                key.startsWith("sleep") -> "Sleep Timer"
                key.startsWith("recent") -> "Recent Searches"
                key.startsWith("stats") -> "Statistics"
                else -> "Other"
            }
            categories[category] = (categories[category] ?: 0) + 1
        }
        
        return buildString {
            append("Backup includes:\n")
            categories.forEach { (category, count) ->
                append("  • $category ($count settings)\n")
            }
        }
    }
}