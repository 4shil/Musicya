package com.fourshil.musicya.ui.album

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
 * ViewModel for AlbumDetailScreen.
 */
@HiltViewModel
class AlbumDetailViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {
    
    private val _album = MutableStateFlow<Album?>(null)
    val album: StateFlow<Album?> = _album.asStateFlow()
    
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadAlbum(albumId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val albumInfo = repository.getAllAlbums().find { it.id == albumId }
                _album.value = albumInfo
                
                val albumSongs = repository.getSongsByAlbum(albumId)
                    .sortedBy { it.trackNumber }
                _songs.value = albumSongs
            } finally {
                _isLoading.value = false
            }
        }
    }
}