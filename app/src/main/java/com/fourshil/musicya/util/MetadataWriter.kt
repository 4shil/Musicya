package com.fourshil.musicya.util

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Utility for editing audio file metadata using JAudioTagger.
 */
object MetadataWriter {

    private const val TAG = "MetadataWriter"

    init {
        // Suppress JAudioTagger logging
        Logger.getLogger("org.jaudiotagger").level = Level.OFF
    }

    /**
     * Update metadata for an audio file.
     * Requires the file path from MediaStore (MediaStore.Audio.Media.DATA).
     */
    suspend fun updateMetadata(
        filePath: String,
        title: String? = null,
        artist: String? = null,
        album: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("File not found: $filePath"))
            }

            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault

            title?.let { tag.setField(FieldKey.TITLE, it) }
            artist?.let { tag.setField(FieldKey.ARTIST, it) }
            album?.let { tag.setField(FieldKey.ALBUM, it) }

            audioFile.commit()
            Log.d(TAG, "Metadata updated successfully for: $filePath")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update metadata for: $filePath", e)
            Result.failure(e)
        }
    }

    /**
     * Update album art for an audio file.
     */
    suspend fun updateAlbumArt(
        filePath: String,
        artUri: Uri
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val file = File(filePath)
            if (!file.exists()) {
                return@withContext Result.failure(Exception("File not found: $filePath"))
            }

            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tagOrCreateAndSetDefault

            val context = android.app.Application() as? android.app.Application
            if (context != null) {
                val inputStream = context.contentResolver.openInputStream(artUri)
                inputStream?.use { stream ->
                    val imageData = stream.readBytes()
                    val picture = org.jaudiotagger.tag.images.ArtworkFactory.createArtworkFromFile(
                        java.io.File(artUri.path ?: "")
                    )
                    picture?.let { tag.deleteField(FieldKey.COVER_ART) }
                    val artwork = org.jaudiotagger.tag.images.ArtworkFactory.createArtworkFromMetadata(imageData)
                    tag.setField(artwork)
                }
            }

            audioFile.commit()
            Log.d(TAG, "Album art updated successfully for: $filePath")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update album art for: $filePath", e)
            Result.failure(e)
        }
    }
}