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
class MostPlayedViewModel @Inject constructor(
    private val musicDao: MusicDao,
    private val musicRepository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    
    private val _mostPlayedSongs = MutableStateFlow<List<Song>>(emptyList())
    val mostPlayedSongs = _mostPlayedSongs.asStateFlow()
    
    val favoriteIds = musicDao.getFavoriteIds()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        loadMostPlayed()
    }
    
    private fun loadMostPlayed() {
        viewModelScope.launch {
            musicDao.getMostPlayedSongIds(50).collect { topIds ->
                _isLoading.value = true
                val songs = musicRepository.getSongsByIds(topIds)
                // Maintain order from topIds (most played first)
                val orderedSongs = topIds.mapNotNull { id -> songs.find { it.id == id } }
                _mostPlayedSongs.value = orderedSongs
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
        val songs = _mostPlayedSongs.value
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
