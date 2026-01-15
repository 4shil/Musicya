package com.fourshil.musicya.data.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.TagOptionSingleton
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun updateTags(
        songUri: Uri,
        path: String,
        newTitle: String,
        newArtist: String,
        newAlbum: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Update MediaStore (System Database)
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.TITLE, newTitle)
                put(MediaStore.Audio.Media.ARTIST, newArtist)
                put(MediaStore.Audio.Media.ALBUM, newAlbum)
            }
            
            // Handle Android 10+ scoped storage: 
            // If we don't own the file, this might throw RecoverableSecurityException which we need to catch in UI.
            // For now, we assume simple update success or catch generic exception.
            val rowsUpdated = context.contentResolver.update(songUri, values, null, null)
            
            if (rowsUpdated == 0) {
                 // Try updating by ID if URI didn't work directly (sometimes URI has parameters)
                 val id = ContentUris.parseId(songUri)
                 val baseUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                 val correctUri = ContentUris.withAppendedId(baseUri, id)
                 context.contentResolver.update(correctUri, values, null, null)
            }

            // 2. Update File Tags (Physical File)
            // Fix for Android strict mode complaining about file URI exposure, though we use path here.
            

            val file = File(path)
            if (file.exists() && file.canWrite()) {
                try {
                    val audioFile = AudioFileIO.read(file)
                    val tag = audioFile.tagOrCreateAndSetDefault
                    
                    tag.setField(FieldKey.TITLE, newTitle)
                    tag.setField(FieldKey.ARTIST, newArtist)
                    tag.setField(FieldKey.ALBUM, newAlbum)
                    
                    audioFile.commit()
                } catch (e: Exception) {
                    Log.e("TagRepository", "Failed to write file tags: ${e.message}")
                    // Don't fail the whole operation if just file write fails, as MediaStore update is primary for App
                }
            } else {
                 Log.w("TagRepository", "File not writable or doesn't exist: $path")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("TagRepository", "Error updating tags", e)
            Result.failure(e)
        }
    }
}
