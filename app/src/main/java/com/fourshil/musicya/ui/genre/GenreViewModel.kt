package com.fourshil.musicya.ui.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for GenreScreen.
 */
@HiltViewModel
class GenreViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {
    
    private val _genres = MutableStateFlow<List<GenreInfo>>(emptyList())
    val genres: StateFlow<List<GenreInfo>> = _genres.asStateFlow()
    
    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre.asStateFlow()
    
    private val _genreSongs = MutableStateFlow<List<Song>>(emptyList())
    val genreSongs: StateFlow<List<Song>> = _genreSongs.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private var allSongs: List<Song>? = null

    init {
        loadGenres()
    }
    
    private fun loadGenres() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val songs = repository.getAllSongs()
                allSongs = songs
                
                // Group songs by genre (from metadata or folder path)
                val genreMap = mutableMapOf<String, Int>()
                songs.forEach { song ->
                    val genre = detectGenre(song)
                    genreMap[genre] = (genreMap[genre] ?: 0) + 1
                }
                
                _genres.value = genreMap.map { (name, count) ->
                    GenreInfo(name, count)
                }.sortedByDescending { it.songCount }
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun detectGenre(song: Song): String {
        // Try to detect genre from file path or metadata
        val path = song.path.lowercase()
        return when {
            path.contains("rock") -> "Rock"
            path.contains("pop") -> "Pop"
            path.contains("jazz") -> "Jazz"
            path.contains("classical") -> "Classical"
            path.contains("hip hop") || path.contains("hip-hop") || path.contains("rap") -> "Hip Hop"
            path.contains("electronic") || path.contains("edm") || path.contains("techno") -> "Electronic"
            path.contains("country") -> "Country"
            path.contains("indie") -> "Indie"
            path.contains("metal") -> "Metal"
            path.contains("r&b") || path.contains("rnb") -> "R&B"
            path.contains("folk") -> "Folk"
            path.contains("blues") -> "Blues"
            path.contains("reggae") -> "Reggae"
            path.contains("latin") -> "Latin"
            path.contains("soundtrack") || path.contains("ost") -> "Soundtrack"
            else -> "Other"
        }
    }
    
    fun selectGenre(genreName: String) {
        _selectedGenre.value = genreName
        val songs = allSongs ?: emptyList()
        _genreSongs.value = songs.filter { detectGenre(it) == genreName }
    }
    
    fun clearSelection() {
        _selectedGenre.value = null
        _genreSongs.value = emptyList()
    }
}