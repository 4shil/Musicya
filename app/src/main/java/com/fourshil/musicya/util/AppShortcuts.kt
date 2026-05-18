package com.fourshil.musicya.util

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.player.PlayerController
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages app shortcuts for Android launcher integration.
 * Supports quick actions from home screen long-press menu.
 */
object ShortcutManager {

    private const val TAG = "ShortcutManager"
    
    private const val SHORTCUT_SHUFFLE_ALL = "shuffle_all"
    private const val SHORTCUT_RECENTLY_PLAYED = "recently_played"
    private const val SHORTCUT_FAVORITES = "favorites"
    private const val SHORTCUT_RANDOM = "random"

    /**
     * Register dynamic shortcuts with the system launcher.
     */
    fun registerShortcuts(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return

        try {
            val shortcutManager = context.getSystemService(ShortcutManager::class.java)
            
            val shortcuts = listOf(
                createShortcut(
                    context,
                    SHORTCUT_SHUFFLE_ALL,
                    "Shuffle All",
                    "Play all songs shuffled",
                    Icons.Default.Shuffle,
                    Intent(context, MainActivity::class.java).apply {
                        action = "com.fourshil.musicya.SHUFFLE_ALL"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                ),
                createShortcut(
                    context,
                    SHORTCUT_RECENTLY_PLAYED,
                    "Recently Played",
                    "View recently played songs",
                    Icons.Default.History,
                    Intent(context, MainActivity::class.java).apply {
                        action = "com.fourshil.musicya.RECENTLY_PLAYED"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                ),
                createShortcut(
                    context,
                    SHORTCUT_FAVORITES,
                    "Favorites",
                    "View your favorite songs",
                    Icons.Default.Favorite,
                    Intent(context, MainActivity::class.java).apply {
                        action = "com.fourshil.musicya.FAVORITES"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                ),
                createShortcut(
                    context,
                    SHORTCUT_RANDOM,
                    "Random Play",
                    "Play a random song",
                    Icons.Default.Casino,
                    Intent(context, MainActivity::class.java).apply {
                        action = "com.fourshil.musicya.RANDOM"
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            )

            shortcutManager?.dynamicShortcuts = shortcuts
            Log.d(TAG, "Registered ${shortcuts.size} dynamic shortcuts")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register shortcuts", e)
        }
    }

    private fun createShortcut(
        context: Context,
        id: String,
        shortLabel: String,
        longLabel: String,
        icon: android.graphics.drawable.Icon,
        intent: Intent
    ): ShortcutInfo {
        return ShortcutInfo.Builder(context, id)
            .setShortLabel(shortLabel)
            .setLongLabel(longLabel)
            .setIcon(icon)
            .setIntent(intent)
            .setRank(if (id == SHORTCUT_SHUFFLE_ALL) 0 else 1)
            .build()
    }
}

/**
 * Queue persistence utilities for saving and restoring playback queue.
 */
object QueuePersistence {

    private const val TAG = "QueuePersistence"
    private const val PREF_NAME = "queue_state"
    private const val KEY_CURRENT_INDEX = "current_index"
    private const val KEY_QUEUE_JSON = "queue_json"
    private const val KEY_POSITION = "current_position"
    private const val KEY_SHUFFLE = "shuffle_enabled"
    private const val KEY_REPEAT = "repeat_mode"

    /**
     * Save current queue state to SharedPreferences.
     */
    fun saveQueueState(
        context: Context,
        songs: List<Song>,
        currentIndex: Int,
        position: Long,
        shuffleEnabled: Boolean,
        repeatMode: Int
    ) {
        try {
            val json = JSONArray()
            songs.forEach { song ->
                json.put(JSONObject().apply {
                    put("id", song.id)
                    put("title", song.title)
                    put("artist", song.artist)
                    put("album", song.album)
                    put("path", song.path)
                    put("duration", song.duration)
                })
            }

            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit()
                .putInt(KEY_CURRENT_INDEX, currentIndex)
                .putString(KEY_QUEUE_JSON, json.toString())
                .putLong(KEY_POSITION, position)
                .putBoolean(KEY_SHUFFLE, shuffleEnabled)
                .putInt(KEY_REPEAT, repeatMode)
                .apply()

            Log.d(TAG, "Saved queue state: ${songs.size} songs, index $currentIndex")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save queue state", e)
        }
    }

    /**
     * Restore queue state from SharedPreferences.
     */
    fun restoreQueueState(context: Context): QueueState? {
        return try {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val jsonString = prefs.getString(KEY_QUEUE_JSON, null) ?: return null

            val json = JSONArray(jsonString)
            val songs = mutableListOf<Song>()
            
            for (i in 0 until json.length()) {
                val obj = json.getJSONObject(i)
                songs.add(
                    Song(
                        id = obj.getLong("id"),
                        title = obj.optString("title", "Unknown"),
                        artist = obj.optString("artist", "Unknown Artist"),
                        album = obj.optString("album", "Unknown Album"),
                        path = obj.optString("path", ""),
                        duration = obj.optLong("duration", 0)
                    )
                )
            }

            val currentIndex = prefs.getInt(KEY_CURRENT_INDEX, 0)
            val position = prefs.getLong(KEY_POSITION, 0)
            val shuffleEnabled = prefs.getBoolean(KEY_SHUFFLE, false)
            val repeatMode = prefs.getInt(KEY_REPEAT, 0)

            Log.d(TAG, "Restored queue state: ${songs.size} songs, index $currentIndex")
            QueueState(songs, currentIndex, position, shuffleEnabled, repeatMode)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to restore queue state", e)
            null
        }
    }

    /**
     * Clear saved queue state.
     */
    fun clearQueueState(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
        Log.d(TAG, "Cleared saved queue state")
    }

    data class QueueState(
        val songs: List<Song>,
        val currentIndex: Int,
        val position: Long,
        val shuffleEnabled: Boolean,
        val repeatMode: Int
    )
}