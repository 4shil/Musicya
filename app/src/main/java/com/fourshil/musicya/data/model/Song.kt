package com.fourshil.musicya.data.model

import android.net.Uri

import androidx.compose.runtime.Immutable

@Immutable
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val uri: Uri,
    val path: String,
    val dateAdded: Long,
    val size: Long
) {
    val durationFormatted: String
        get() {
            val minutes = (duration / 1000) / 60
            val seconds = (duration / 1000) % 60
            return "%d:%02d".format(minutes, seconds)
        }
    
    val albumArtUri: Uri
        get() = Uri.parse("content://media/external/audio/albumart/$albumId")
}

@Immutable
data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val songCount: Int,
    val year: Int
) {
    val artUri: Uri
        get() = Uri.parse("content://media/external/audio/albumart/$id")
}

@Immutable
data class Artist(
    val id: Long,
    val name: String,
    val songCount: Int,
    val albumCount: Int
)

@Immutable
data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int,
    val isSystem: Boolean = false // For "Recently Added", "Most Played" etc.
)

@Immutable
data class Folder(
    val path: String,
    val name: String,
    val songCount: Int
)
