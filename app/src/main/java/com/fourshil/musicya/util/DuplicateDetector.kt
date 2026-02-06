package com.fourshil.musicya.util

import com.fourshil.musicya.data.model.Song

/**
 * Detects duplicate songs in the library based on various criteria.
 */
object DuplicateDetector {

    data class DuplicateGroup(
        val key: String,
        val songs: List<Song>,
        val matchType: MatchType
    )

    enum class MatchType {
        EXACT,      // Same title, artist, and duration
        TITLE_ARTIST, // Same title and artist
        TITLE_ONLY,   // Same title only
        FINGERPRINT   // Similar duration and title
    }

    /**
     * Find all duplicate groups in a list of songs.
     */
    fun findDuplicates(songs: List<Song>): List<DuplicateGroup> {
        val groups = mutableListOf<DuplicateGroup>()

        // Check exact matches (title + artist + duration within 2s)
        val exactGroups = songs.groupBy {
            "${it.title.lowercase().trim()}_${it.artist.lowercase().trim()}_${it.duration / 2000}"
        }.filter { it.value.size > 1 }
        exactGroups.forEach { (key, group) ->
            groups.add(DuplicateGroup(key, group, MatchType.EXACT))
        }

        // Check title+artist matches (excluding already found exact matches)
        val exactSongIds = groups.flatMap { it.songs }.map { it.id }.toSet()
        val remaining = songs.filter { it.id !in exactSongIds }
        val titleArtistGroups = remaining.groupBy {
            "${it.title.lowercase().trim()}_${it.artist.lowercase().trim()}"
        }.filter { it.value.size > 1 }
        titleArtistGroups.forEach { (key, group) ->
            groups.add(DuplicateGroup(key, group, MatchType.TITLE_ARTIST))
        }

        return groups
    }

    /**
     * Find potential duplicates for a specific song.
     */
    fun findDuplicatesForSong(song: Song, allSongs: List<Song>): List<Song> {
        return allSongs.filter { other ->
            other.id != song.id && isPotentialDuplicate(song, other)
        }
    }

    /**
     * Check if two songs are potential duplicates.
     */
    fun isPotentialDuplicate(a: Song, b: Song): Boolean {
        val titleMatch = a.title.lowercase().trim() == b.title.lowercase().trim()
        val artistMatch = a.artist.lowercase().trim() == b.artist.lowercase().trim()
        val durationMatch = kotlin.math.abs(a.duration - b.duration) < 3000 // within 3 seconds

        return titleMatch && artistMatch && durationMatch
    }

    /**
     * Get a summary of duplicates found.
     */
    fun getDuplicateSummary(songs: List<Song>): String {
        val groups = findDuplicates(songs)
        if (groups.isEmpty()) return "No duplicates found."

        val totalDuplicates = groups.sumOf { it.songs.size - 1 }
        val exactCount = groups.count { it.matchType == MatchType.EXACT }
        val titleArtistCount = groups.count { it.matchType == MatchType.TITLE_ARTIST }

        return buildString {
            append("Found ${groups.size} duplicate groups ($totalDuplicates extra songs)\n")
            if (exactCount > 0) append("  • $exactCount exact matches\n")
            if (titleArtistCount > 0) append("  • $titleArtistCount title+artist matches")
        }
    }
}