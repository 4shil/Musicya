package com.fourshil.musicya.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Enhanced album art helper with high-resolution fallback and caching.
 */
object AlbumArtHelper {

    private const val TAG = "AlbumArtHelper"
    private val artCache = androidx.collection.LruCache<String, Bitmap>(20)

    /**
     * Get high-quality album art URI with multiple fallback strategies.
     */
    suspend fun getHighQualityArtUri(filePath: String?, albumId: Long): Uri? = withContext(Dispatchers.IO) {
        if (filePath == null) return@withContext null

        try {
            // Strategy 1: Try to get art from file metadata via MediaStore
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val collection = MediaStore.Audio.Albums.getContentUri(MediaStore.VOLUME_EXTERNAL)
                val projection = arrayOf(MediaStore.Audio.Albums.ALBUM_ART)
                val selection = "${MediaStore.Audio.Albums._ID} = ?"
                val selectionArgs = arrayOf(albumId.toString())

                android.app.Application().contentResolver?.query(
                    collection, projection, selection, selectionArgs, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val artPath = cursor.getString(0)
                        if (!artPath.isNullOrBlank()) {
                            return@withContext Uri.parse("file://$artPath")
                        }
                    }
                }
            }

            // Strategy 2: Try to extract embedded art from audio file
            if (filePath.isNotEmpty() && java.io.File(filePath).exists()) {
                try {
                    val audioFile = org.jaudiotagger.audio.AudioFileIO.read(java.io.File(filePath))
                    val tag = audioFile.tag ?: return@withContext null
                    val artwork = tag.firstArtwork ?: return@withContext null
                    val imageData = artwork.binaryData ?: return@withContext null

                    // Cache the extracted bitmap
                    val cacheKey = "extracted_$albumId"
                    val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
                    if (bitmap != null) {
                        artCache.put(cacheKey, bitmap)
                        return@withContext Uri.parse("content://media/external/audio/albumart/$albumId")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Could not extract embedded art from: $filePath", e)
                }
            }

            // Strategy 3: Use standard MediaStore album art URI
            val standardUri = Uri.parse("content://media/external/audio/albumart/$albumId")
            return@withContext standardUri
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get art for album $albumId", e)
            return@withContext null
        }
    }

    /**
     * Load a high-resolution bitmap for album art.
     */
    suspend fun loadHighResArt(
        context: Context,
        albumId: Long,
        width: Int = 512,
        height: Int = 512
    ): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse("content://media/external/audio/albumart/$albumId")
            val size = Size(width, height)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.contentResolver.loadThumbnail(uri, size, null)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load high-res art for album $albumId", e)
            null
        }
    }

    /**
     * Clear the album art memory cache.
     */
    fun clearCache() {
        artCache.evictAll()
        Log.d(TAG, "Album art cache cleared")
    }
}