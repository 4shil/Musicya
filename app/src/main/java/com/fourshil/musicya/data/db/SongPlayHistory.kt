package com.fourshil.musicya.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity tracking play history for songs.
 * Stores play count and last played timestamp for "Most Played" and "Recently Played" features.
 */
@Entity(tableName = "song_play_history")
data class SongPlayHistory(
    @PrimaryKey
    val songId: Long,
    val playCount: Int = 0,
    val lastPlayedAt: Long = 0L
)
