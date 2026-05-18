package com.fourshil.musicya.util

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Network utilities for fetching lyrics and online data.
 */
object NetworkUtils {

    private const val TAG = "NetworkUtils"
    private const val TIMEOUT_MS = 10000
    private const val MAX_RETRIES = 2

    /**
     * Fetch lyrics for a song from an online API.
     */
    suspend fun fetchLyrics(
        title: String,
        artist: String,
        apiUrl: String = "https://api.lyrics.ovh/v1"
    ): Result<String> = withContext(Dispatchers.IO) {
        var lastError: Exception? = null

        for (attempt in 1..MAX_RETRIES) {
            try {
                val encodedArtist = URLEncoder.encode(artist, "UTF-8")
                val encodedTitle = URLEncoder.encode(title, "UTF-8")
                val urlString = "$apiUrl/$encodedArtist/$encodedTitle"

                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                    setRequestProperty("Accept", "application/json")
                    setRequestProperty("User-Agent", "Musicya/1.0")
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().readText()
                    connection.disconnect()

                    // Parse JSON response
                    val json = Json { ignoreUnknownKeys = true }
                    val lyricsResponse = json.decodeFromString<LyricsResponse>(response)
                    return@withContext Result.success(lyricsResponse.lyrics)
                } else {
                    Log.w(TAG, "Lyrics fetch failed with code: $responseCode")
                }
                connection.disconnect()
            } catch (e: Exception) {
                lastError = e
                Log.e(TAG, "Attempt $attempt failed: ${e.message}")
                if (attempt < MAX_RETRIES) {
                    kotlinx.coroutines.delay(500 * attempt) // Exponential backoff
                }
            }
        }

        Result.failure(lastError ?: Exception("Failed to fetch lyrics after $MAX_RETRIES attempts"))
    }

    @kotlinx.serialization.Serializable
    private data class LyricsResponse(val lyrics: String)

    /**
     * Check if network is available (basic connectivity check).
     */
    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val runtime = Runtime.getRuntime()
            val process = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = process.waitFor()
            exitValue == 0
        } catch (e: Exception) {
            false
        }
    }
}

/**
 * Result wrapper for network operations.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}