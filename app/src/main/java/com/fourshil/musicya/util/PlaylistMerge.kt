package com.fourshil.musicya.util

import android.content.Context
import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.db.Playlist
import com.fourshil.musicya.data.db.PlaylistSong
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File

/**
 * Playlist merge utility for combining multiple playlists.
 */
object PlaylistMerge {

    data class MergeOptions(
        val removeDuplicates: Boolean = true,
        val sortBy: SortOption = SortOption.ORIGINAL,
        val newName: String = "Merged Playlist"
    )

    enum class SortOption {
        ORIGINAL, TITLE, ARTIST, ALBUM, DURATION, DATE_ADDED, SHUFFLE
    }

    /**
     * Merge multiple playlists into a new playlist.
     */
    suspend fun mergePlaylists(
        repository: MusicRepository,
        musicDao: MusicDao,
        playlistIds: List<Long>,
        options: MergeOptions
    ): Result<Long> {
        return try {
            // Collect all songs from selected playlists
            val allSongs = mutableListOf<Song>()
            
            playlistIds.forEach { playlistId ->
                val songs = musicDao.getPlaylistSongsList(playlistId)
                allSongs.addAll(songs)
            }

            // Remove duplicates if requested
            val finalSongs = if (options.removeDuplicates) {
                allSongs.distinctBy { it.id }
            } else {
                allSongs
            }

            // Sort according to option
            val sortedSongs = when (options.sortBy) {
                SortOption.ORIGINAL -> finalSongs
                SortOption.TITLE -> finalSongs.sortedBy { it.title.lowercase() }
                SortOption.ARTIST -> finalSongs.sortedBy { it.artist.lowercase() }
                SortOption.ALBUM -> finalSongs.sortedBy { it.album.lowercase() }
                SortOption.DURATION -> finalSongs.sortedByDescending { it.duration }
                SortOption.DATE_ADDED -> finalSongs // Already in order
                SortOption.SHUFFLE -> finalSongs.shuffled()
            }

            // Create new playlist
            val newPlaylistId = musicDao.createPlaylist(
                Playlist(name = options.newName)
            )

            // Add songs to new playlist
            sortedSongs.forEachIndexed { index, song ->
                musicDao.addSongToPlaylist(
                    PlaylistSong(
                        playlistId = newPlaylistId,
                        songId = song.id,
                        position = index
                    )
                )
            }

            Log.d("PlaylistMerge", "Merged ${playlistIds.size} playlists into '${options.newName}' with ${sortedSongs.size} songs")
            Result.success(newPlaylistId)
        } catch (e: Exception) {
            Log.e("PlaylistMerge", "Failed to merge playlists", e)
            Result.failure(e)
        }
    }

    /**
     * Get merge preview showing what the merged playlist would look like.
     */
    fun getMergePreview(
        playlists: List<Pair<Long, String>>,
        allSongs: Map<Long, List<Song>>,
        options: MergeOptions
    ): MergePreview {
        val totalSongs = playlists.sumOf { (id, _) -> allSongs[id]?.size ?: 0 }
        val uniqueSongs = playlists.flatMap { (id, _) -> allSongs[id] ?: emptyList() }
            .distinctBy { it.id }
            .size
        
        return MergePreview(
            sourcePlaylistCount = playlists.size,
            totalSongs = totalSongs,
            uniqueSongs = uniqueSongs,
            duplicatesRemoved = totalSongs - uniqueSongs,
            estimatedDuration = allSongs.values.flatten().distinctBy { it.id }
                .sumOf { it.duration }
        )
    }

    data class MergePreview(
        val sourcePlaylistCount: Int,
        val totalSongs: Int,
        val uniqueSongs: Int,
        val duplicatesRemoved: Int,
        val estimatedDuration: Long
    )
}

/**
 * Folder sync utility for monitoring music directories.
 */
class FolderSyncManager(
    private val context: Context,
    private val repository: MusicRepository,
    private val onChangesDetected: (added: Int, removed: Int) -> Unit
) {
    companion object {
        private const val TAG = "FolderSync"
    }

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var fileObserver: RecursiveFileObserver? = null
    private val handler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null

    /**
     * Start monitoring a folder for changes.
     */
    fun startWatching(folderPath: String) {
        stopWatching()
        
        try {
            val folder = File(folderPath)
            if (!folder.exists() || !folder.isDirectory) {
                Log.w(TAG, "Folder does not exist: $folderPath")
                return
            }

            fileObserver = RecursiveFileObserver(folderPath) { event, path ->
                // Debounce rapid file system changes
                debounceRunnable?.let { handler.removeCallbacks(it) }
                debounceRunnable = Runnable {
                    Log.d(TAG, "File change detected: $event at $path")
                    syncFolder(folderPath)
                }.also { handler.postDelayed(it, 2000) }
            }
            fileObserver?.startWatching()
            Log.d(TAG, "Started watching folder: $folderPath")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start folder watcher", e)
        }
    }

    /**
     * Stop monitoring folder.
     */
    fun stopWatching() {
        try {
            fileObserver?.stopWatching()
            fileObserver = null
            debounceRunnable?.let { handler.removeCallbacks(it) }
            Log.d(TAG, "Stopped folder watcher")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop folder watcher", e)
        }
    }

    /**
     * Manually trigger a folder sync.
     */
    fun syncFolder(folderPath: String) {
        scope.launch {
            try {
                val before = repository.getAllSongs().first().size
                repository.clearCache()
                repository.scanMediaStore()
                val after = repository.getAllSongs().first().size
                
                val added = after - before
                if (added != 0) {
                    Log.d(TAG, "Sync complete: $added songs added/removed")
                    onChangesDetected(maxOf(added, 0), maxOf(-added, 0))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Folder sync failed", e)
            }
        }
    }

    /**
     * Recursive file observer for monitoring directory changes.
     */
    private class RecursiveFileObserver(
        private val rootPath: String,
        private val onEvent: (Int, String) -> Unit
    ) : FileObserver(rootPath, CREATE or DELETE or MODIFY or MOVED_FROM or MOVED_TO) {

        private val observers = mutableListOf<FileObserver>()

        override fun startWatching() {
            super.startWatching()
            // Watch subdirectories
            File(rootPath).walkTopDown()
                .filter { it.isDirectory && it.path != rootPath }
                .forEach { dir ->
                    val observer = object : FileObserver(dir.path, CREATE or DELETE or MODIFY or MOVED_FROM or MOVED_TO) {
                        override fun onEvent(event: Int, path: String?) {
                            if (path != null) {
                                this@RecursiveFileObserver.onEvent(event, "${dir.path}/$path")
                            }
                        }
                    }
                    observer.startWatching()
                    observers.add(observer)
                }
        }

        override fun stopWatching() {
            super.stopWatching()
            observers.forEach { it.stopWatching() }
            observers.clear()
        }

        override fun onEvent(event: Int, path: String?) {
            if (path != null) {
                onEvent(event, "$rootPath/$path")
            }
        }
    }
}