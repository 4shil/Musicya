package com.fourshil.musicya.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import com.fourshil.musicya.player.PlayerController
import com.fourshil.musicya.util.FuzzySearch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository,
    private val playerController: PlayerController
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    val albums = _albums.asStateFlow()

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists = _artists.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches = _recentSearches.asStateFlow()

    // Lazy-loaded data - only fetched when user starts typing
    private var allSongs: List<Song>? = null
    private var allAlbums: List<Album>? = null
    private var allArtists: List<Artist>? = null
    private var dataLoaded = false

    init {
        playerController.connect()
        observeQuery()
    }

    private suspend fun ensureDataLoaded() {
        if (dataLoaded) return
        withContext(Dispatchers.IO) {
            allSongs = repository.getAllSongs()
            allAlbums = repository.getAllAlbums()
            allArtists = repository.getAllArtists()
            dataLoaded = true
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            _query
                .debounce(300)
                .collect { q ->
                    if (q.isBlank()) {
                        _songs.value = emptyList()
                        _albums.value = emptyList()
                        _artists.value = emptyList()
                    } else {
                        _isSearching.value = true
                        // Lazy load data only when user starts searching
                        ensureDataLoaded()

                        // Use fuzzy search for better matching
                        _songs.value = allSongs
                            ?.filter { song ->
                                FuzzySearch.matches(q, song.title) ||
                                FuzzySearch.matches(q, song.artist) ||
                                FuzzySearch.matches(q, song.album)
                            }
                            ?.sortedByDescending { song ->
                                maxOf(
                                    FuzzySearch.score(q, song.title),
                                    FuzzySearch.score(q, song.artist)
                                )
                            }
                            ?.take(20) ?: emptyList()

                        _albums.value = allAlbums
                            ?.filter { album ->
                                FuzzySearch.matches(q, album.name) ||
                                FuzzySearch.matches(q, album.artist)
                            }
                            ?.sortedByDescending { album ->
                                maxOf(
                                    FuzzySearch.score(q, album.name),
                                    FuzzySearch.score(q, album.artist)
                                )
                            }
                            ?.take(10) ?: emptyList()

                        _artists.value = allArtists
                            ?.filter { artist ->
                                FuzzySearch.matches(q, artist.name)
                            }
                            ?.sortedByDescending { artist ->
                                FuzzySearch.score(q, artist.name)
                            }
                            ?.take(10) ?: emptyList()

                        _isSearching.value = false
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }

    fun saveRecentSearch(queryText: String) {
        if (queryText.isBlank()) return
        val current = _recentSearches.value.toMutableList()
        current.remove(queryText) // Avoid duplicates
        current.add(0, queryText) // Add to front
        _recentSearches.value = current.take(10) // Keep max 10
    }

    fun clearRecentSearches() {
        _recentSearches.value = emptyList()
    }

    fun playSong(song: Song) {
        playerController.playSong(song)
    }

    fun playAlbum(albumId: Long) {
        viewModelScope.launch {
            val albumSongs = repository.getSongsByAlbum(albumId)
            if (albumSongs.isNotEmpty()) {
                playerController.playSongs(albumSongs)
            }
        }
    }
}
