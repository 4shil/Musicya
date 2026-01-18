package com.fourshil.musicya.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.db.MusicDao
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NeverPlayedViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val musicRepository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    // Sort options could be added later (e.g. by Added Date, Name, etc.)
    // For now, default to Random or Name
    private val _neverPlayedSongs = MutableStateFlow<List<Song>>(emptyList())
    val neverPlayedSongs = _neverPlayedSongs.asStateFlow()
    
    val favoriteIds = musicDao.getFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        loadNeverPlayed()
    }
    
    private fun loadNeverPlayed() {
        viewModelScope.launch {
            musicDao.getAllPlayedSongIds().collect { playedIds ->
                _isLoading.value = true
                val allSongs = musicRepository.getAllSongs()
                val playedIdSet = playedIds.toSet()
                
                // transform to list and filter
                val unplayed = allSongs.filter { it.id !in playedIdSet }
                    .shuffled() // Shuffle to make it interesting, or sort by name?
                    // Usually "Never Played" is exploring forgotten music. Shuffle is good.
                    // But maybe sort by date added?
                    // Let's sort by Date Added DESC (Newest unplayed first) or ASC (Oldest unplayed first)?
                    // "Dusty" implies Oldest.
                    // Let's do random for now, or Date Added ASC (Oldest first).
                    // Actually, random is fun for "Smart Playlist".
                    // But standard library usually is sorted.
                    // I'll sort by Artist then Title for stability.
                    .sortedWith(compareBy({ it.artist }, { it.title }))
                
                _neverPlayedSongs.value = unplayed
                _isLoading.value = false
            }
        }
    }
    
    fun toggleFavorite(songId: Long) {
        viewModelScope.launch {
            musicDao.toggleFavorite(songId)
        }
    }
    
    fun playSongAt(index: Int) {
        val songs = _neverPlayedSongs.value
        if (songs.isNotEmpty() && index in songs.indices) {
            playerController.playSongs(songs, index)
        }
    }
    
    fun playNext(song: Song) {
        playerController.playNext(song)
    }
    
    fun addToQueue(song: Song) {
        playerController.addToQueue(song)
    }
}
