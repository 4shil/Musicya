package com.fourshil.musicya.util

import com.fourshil.musicya.data.model.Song

/**
 * Smart sorting options for playlists and song lists.
 */
object PlaylistSort {

    enum class SortOption(val label: String) {
        NAME_ASC("Name (A-Z)"),
        NAME_DESC("Name (Z-A)"),
        ARTIST_ASC("Artist (A-Z)"),
        ARTIST_DESC("Artist (Z-A)"),
        ALBUM_ASC("Album (A-Z)"),
        ALBUM_DESC("Album (Z-A)"),
        DATE_ADDED_DESC("Recently Added"),
        DATE_ADDED_ASC("Oldest First"),
        DURATION_DESC("Longest First"),
        DURATION_ASC("Shortest First"),
        MOST_PLAYED("Most Played"),
        LEAST_PLAYED("Least Played"),
        RECENTLY_PLAYED("Recently Played"),
        NEVER_PLAYED("Never Played")
    }

    /**
     * Sort a list of songs by the given option.
     */
    fun sort(songs: List<Song>, option: SortOption): List<Song> {
        return when (option) {
            SortOption.NAME_ASC -> songs.sortedBy { it.title.lowercase() }
            SortOption.NAME_DESC -> songs.sortedByDescending { it.title.lowercase() }
            SortOption.ARTIST_ASC -> songs.sortedBy { it.artist.lowercase() }
            SortOption.ARTIST_DESC -> songs.sortedByDescending { it.artist.lowercase() }
            SortOption.ALBUM_ASC -> songs.sortedBy { it.album.lowercase() }
            SortOption.ALBUM_DESC -> songs.sortedByDescending { it.album.lowercase() }
            SortOption.DATE_ADDED_DESC -> songs.sortedByDescending { it.dateAdded }
            SortOption.DATE_ADDED_ASC -> songs.sortedBy { it.dateAdded }
            SortOption.DURATION_DESC -> songs.sortedByDescending { it.duration }
            SortOption.DURATION_ASC -> songs.sortedBy { it.duration }
            SortOption.MOST_PLAYED -> songs.sortedByDescending { PlaybackStats.getPlayCount(it.id) }
            SortOption.LEAST_PLAYED -> songs.sortedBy { PlaybackStats.getPlayCount(it.id) }
            SortOption.RECENTLY_PLAYED -> songs.sortedByDescending { PlaybackStats.getLastPlayed(it.id) }
            SortOption.NEVER_PLAYED -> {
                songs.filter { PlaybackStats.getPlayCount(it.id) == 0 } +
                songs.filter { PlaybackStats.getPlayCount(it.id) > 0 }
            }
        }
    }

    /**
     * Get all sort options as a list for UI dropdowns.
     */
    fun allOptions(): List<SortOption> = entries

    /**
     * Smart shuffle - sorts by play count then randomly shuffles top.
     * Favorites songs that have been played less (discovery mode).
     */
    fun smartShuffle(songs: List<Song>): List<Song> {
        if (songs.isEmpty()) return emptyList()
        
        // Separate never-played from played
        val neverPlayed = songs.filter { PlaybackStats.getPlayCount(it.id) == 0 }.shuffled()
        val played = songs.filter { PlaybackStats.getPlayCount(it.id) > 0 }
            .sortedBy { PlaybackStats.getPlayCount(it.id) }
            .shuffled()
        
        // Mix them with never-played slightly favored
        return listOf(neverPlayed, played)
            .flatten()
            .shuffled()
    }
}