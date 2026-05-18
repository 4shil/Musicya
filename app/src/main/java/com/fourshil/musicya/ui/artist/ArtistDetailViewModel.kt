package com.fourshil.musicya.ui.artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for ArtistDetailScreen.
 */
@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {
    
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums: StateFlow<List<Album>> = _albums.asStateFlow()
    
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadArtist(artistName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val artistAlbums = repository.getAllAlbums()
                    .filter { it.artist.equals(artistName, ignoreCase = true) }
                _albums.value = artistAlbums
                
                val artistSongs = repository.getSongsByArtist(artistName)
                    .sortedBy { it.album }
                _songs.value = artistSongs
            } finally {
                _isLoading.value = false
            }
        }
    }
}