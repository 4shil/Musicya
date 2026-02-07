package com.fourshil.musicya.util

import com.fourshil.musicya.data.model.Song

/**
 * Queue management utilities.
 */
object QueueUtils {

    /**
     * Calculate total duration of a list of songs.
     */
    fun totalDuration(songs: List<Song>): Long {
        return songs.sumOf { it.duration }
    }

    /**
     * Format duration in human readable format.
     */
    fun formatDuration(ms: Long): String {
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            minutes > 0 -> "${minutes}m ${seconds}s"
            else -> "${seconds}s"
        }
    }

    /**
     * Format song count with proper pluralization.
     */
    fun formatSongCount(count: Int): String {
        return when (count) {
            0 -> "No songs"
            1 -> "1 song"
            else -> "$count songs"
        }
    }

    /**
     * Shuffle a list while keeping track of current song position.
     */
    fun <T> shuffleAround(list: List<T>, currentIndex: Int): List<T> {
        if (list.isEmpty()) return emptyList()
        
        val current = list[currentIndex]
        val remaining = list.filterIndexed { index, _ -> index != currentIndex }.shuffled()
        return listOf(current) + remaining
    }

    /**
     * Get a preview of the next N songs.
     */
    fun getQueuePreview(
        queue: List<Song>,
        currentIndex: Int,
        count: Int = 5
    ): List<Song> {
        if (queue.isEmpty() || currentIndex < 0) return emptyList()
        return queue.drop(currentIndex + 1).take(count)
    }
}