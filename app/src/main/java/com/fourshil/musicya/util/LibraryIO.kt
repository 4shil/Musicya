package com.fourshil.musicya.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.fourshil.musicya.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Library import/export for backing up and restoring music library metadata.
 */
object LibraryIO {

    private const val TAG = "LibraryIO"
    private const val EXPORT_VERSION = 1

    /**
     * Export library metadata to JSON file.
     */
    suspend fun exportLibrary(
        context: Context,
        songs: List<Song>,
        uri: Uri
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val exportData = JSONObject().apply {
                put("version", EXPORT_VERSION)
                put("app", "Musicya")
                put("exportedAt", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
                put("songCount", songs.size)
                put("songs", JSONArray().apply {
                    songs.forEach { song ->
                        put(JSONObject().apply {
                            put("id", song.id)
                            put("title", song.title)
                            put("artist", song.artist)
                            put("album", song.album)
                            put("duration", song.duration)
                            put("path", song.path)
                            put("fileSize", song.fileSize)
                            put("bitrate", song.bitrate)
                            put("sampleRate", song.sampleRate)
                            put("channels", song.channels)
                        })
                    }
                })
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(exportData.toString(2).toByteArray())
            }

            Log.d(TAG, "Exported ${songs.size} songs to library backup")
            Result.success(songs.size)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export library", e)
            Result.failure(e)
        }
    }

    /**
     * Import library metadata from JSON file.
     */
    suspend fun importLibrary(
        context: Context,
        uri: Uri
    ): Result<ImportedLibrary> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: return@withContext Result.failure(Exception("Cannot read file"))

            val json = JSONObject(jsonString)
            val version = json.optInt("version", 1)
            val songCount = json.optInt("songCount", 0)
            val exportedAt = json.optString("exportedAt", "Unknown")

            val songsArray = json.optJSONArray("songs") ?: JSONArray()
            val songs = mutableListOf<Song>()

            for (i in 0 until songsArray.length()) {
                val obj = songsArray.getJSONObject(i)
                songs.add(
                    Song(
                        id = obj.getLong("id"),
                        title = obj.optString("title", "Unknown"),
                        artist = obj.optString("artist", "Unknown Artist"),
                        album = obj.optString("album", "Unknown Album"),
                        duration = obj.optLong("duration", 0),
                        path = obj.optString("path", ""),
                        fileSize = obj.optLong("fileSize", 0),
                        bitrate = obj.optInt("bitrate", 0),
                        sampleRate = obj.optInt("sampleRate", 0),
                        channels = obj.optInt("channels", 2)
                    )
                )
            }

            Log.d(TAG, "Imported ${songs.size} songs from library backup (exported: $exportedAt)")
            Result.success(ImportedLibrary(songs, songCount, exportedAt, version))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import library", e)
            Result.failure(e)
        }
    }

    data class ImportedLibrary(
        val songs: List<Song>,
        val expectedCount: Int,
        val exportedAt: String,
        val version: Int
    )
}

/**
 * Chromecast support utilities for casting to external speakers.
 */
object ChromecastManager {

    private const val TAG = "ChromecastManager"

    /**
     * Check if Chromecast is available on the network.
     */
    fun isChromecastAvailable(context: Context): Boolean {
        // In a real implementation, this would check for Cast SDK availability
        // For now, return false as placeholder
        return try {
            Class.forName("com.google.android.gms.cast.framework.CastContext")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    /**
     * Get list of available cast devices.
     */
    fun getAvailableDevices(): List<CastDevice> {
        // Placeholder - would integrate with Cast SDK
        return emptyList()
    }

    /**
     * Start casting to a device.
     */
    fun startCasting(deviceId: String, song: Song): Boolean {
        Log.d(TAG, "Starting cast to device $deviceId for song ${song.title}")
        // Would integrate with Cast SDK here
        return true
    }

    /**
     * Stop casting.
     */
    fun stopCasting() {
        Log.d(TAG, "Stopping cast")
    }

    /**
     * Check if currently casting.
     */
    fun isCasting(): Boolean {
        return false
    }

    data class CastDevice(
        val id: String,
        val name: String,
        val type: DeviceType
    )

    enum class DeviceType {
        CHROMECAST,
        CHROMECAST_AUDIO,
        CHROMECAST_GROUP,
        SPEAKER,
        TV
    }
}