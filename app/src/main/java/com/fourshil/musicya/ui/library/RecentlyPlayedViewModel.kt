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
class RecentlyPlayedViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val musicRepository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    private val _recentSongs = MutableStateFlow<List<Song>>(emptyList())
    val recentSongs = _recentSongs.asStateFlow()
    
    val favoriteIds = musicDao.getFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        loadRecentlyPlayed()
    }
    
    private fun loadRecentlyPlayed() {
        viewModelScope.launch {
            musicDao.getRecentlyPlayedSongIds(50).collect { recentIds ->
                _isLoading.value = true
                val songs = musicRepository.getSongsByIds(recentIds)
                // Maintain order from recentIds (most recent first)
                val orderedSongs = recentIds.mapNotNull { id -> songs.find { it.id == id } }
                _recentSongs.value = orderedSongs
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
        val songs = _recentSongs.value
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
