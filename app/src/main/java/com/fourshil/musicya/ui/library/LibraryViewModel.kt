package com.fourshil.musicya.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Folder
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import com.fourshil.musicya.player.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists = _artists.asStateFlow()

    private val _folders = MutableStateFlow<List<Folder>>(emptyList())
    val folders = _folders.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        playerController.connect()
        loadLibrary()
    }

    fun loadLibrary() {
        viewModelScope.launch {
            _isLoading.value = true
            _songs.value = repository.getAllSongs()
            _albums.value = repository.getAllAlbums()
            _artists.value = repository.getAllArtists()
            _folders.value = repository.getFolders()
            _isLoading.value = false
        }
    }

    fun playSong(song: Song) {
        playerController.playSong(song)
    }

    fun playSongAt(index: Int) {
        val allSongs = _songs.value
        if (index in allSongs.indices) {
            playerController.playSongs(allSongs, index)
        }
    }

    fun playAlbum(albumId: Long) {
        viewModelScope.launch {
            val albumSongs = repository.getSongsByAlbum(albumId)
            if (albumSongs.isNotEmpty()) {
                playerController.playSongs(albumSongs)
            }
        }
    }

    fun playArtist(artistName: String) {
        viewModelScope.launch {
            val artistSongs = repository.getSongsByArtist(artistName)
            if (artistSongs.isNotEmpty()) {
                playerController.playSongs(artistSongs)
            }
        }
    }

    fun playFolder(folderPath: String) {
        viewModelScope.launch {
            val folderSongs = repository.getSongsByFolder(folderPath)
            if (folderSongs.isNotEmpty()) {
                playerController.playSongs(folderSongs)
            }
        }
    }
}
