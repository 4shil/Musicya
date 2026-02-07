package com.fourshil.musicya.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.fourshil.musicya.data.db.Playlist
import com.fourshil.musicya.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Export and import playlists to/from JSON files.
 */
object PlaylistIO {

    private const val TAG = "PlaylistIO"
    private const val VERSION = 1

    /**
     * Export a playlist to JSON file.
     */
    suspend fun exportPlaylist(
        context: Context,
        playlist: Playlist,
        songs: List<Song>,
        uri: Uri
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("version", VERSION)
                put("name", playlist.name)
                put("description", playlist.description)
                put("createdAt", playlist.createdAt)
                put("songs", JSONArray().apply {
                    songs.forEach { song ->
                        put(JSONObject().apply {
                            put("id", song.id)
                            put("title", song.title)
                            put("artist", song.artist)
                            put("album", song.album)
                            put("duration", song.duration)
                            put("path", song.path)
                        })
                    }
                })
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toString(2).toByteArray())
            }
            Log.d(TAG, "Exported playlist '${playlist.name}' with ${songs.size} songs")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to export playlist", e)
            false
        }
    }

    /**
     * Import a playlist from JSON file.
     */
    suspend fun importPlaylist(
        context: Context,
        uri: Uri
    ): ImportedPlaylist? = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: return@withContext null

            val json = JSONObject(jsonString)
            val version = json.optInt("version", 1)
            
            val name = json.optString("name", "Imported Playlist")
            val description = json.optString("description", "")
            val createdAt = json.optLong("createdAt", System.currentTimeMillis())
            
            val songsArray = json.optJSONArray("songs") ?: JSONArray()
            val songPaths = mutableListOf<String>()
            
            for (i in 0 until songsArray.length()) {
                val songObj = songsArray.getJSONObject(i)
                val path = songObj.optString("path", "")
                if (path.isNotEmpty()) {
                    songPaths.add(path)
                }
            }

            Log.d(TAG, "Imported playlist '$name' with ${songPaths.size} song references")
            ImportedPlaylist(name, description, createdAt, songPaths)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to import playlist", e)
            null
        }
    }

    /**
     * Share playlist as JSON file.
     */
    fun sharePlaylist(context: Context, playlist: Playlist, songs: List<Song>) {
        try {
            val json = JSONObject().apply {
                put("version", VERSION)
                put("name", playlist.name)
                put("description", playlist.description)
                put("songs", JSONArray().apply {
                    songs.forEach { song ->
                        put(JSONObject().apply {
                            put("title", song.title)
                            put("artist", song.artist)
                            put("album", song.album)
                        })
                    }
                })
            }

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_TEXT, json.toString(2))
                putExtra(Intent.EXTRA_SUBJECT, "Playlist: ${playlist.name}")
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Playlist"))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to share playlist", e)
        }
    }

    data class ImportedPlaylist(
        val name: String,
        val description: String,
        val createdAt: Long,
        val songPaths: List<String>
    )
}