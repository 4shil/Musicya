package com.fourshil.musicya.ui.genre

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.util.FuzzySearch

/**
 * Genre browser screen - groups songs by detected genre from file path.
 */
@Composable
fun GenreScreen(
    viewModel: GenreViewModel = hiltViewModel(),
    onSongClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val genres by viewModel.genres.collectAsState()
    val selectedGenre by viewModel.selectedGenre.collectAsState()
    val genreSongs by viewModel.genreSongs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedGenre ?: "Genres") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedGenre != null) viewModel.clearSelection()
                        else onBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (selectedGenre != null) {
            // Songs for selected genre
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                item {
                    Text(
                        text = "${genreSongs.size} songs",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(NeoDimens.ScreenPadding)
                    )
                }
                items(genreSongs, key = { it.id }) { song ->
                    SongListItem(
                        song = song,
                        isFavorite = false,
                        isPlaying = false,
                        onClick = { onSongClick(song.id) },
                        onMoreClick = { }
                    )
                }
            }
        } else {
            // Genre list
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(genres) { genre ->
                    ListItem(
                        headlineContent = {
                            Text(genre.name, style = MaterialTheme.typography.bodyLarge)
                        },
                        supportingContent = {
                            Text("${genre.songCount} songs")
                        },
                        leadingContent = {
                            Icon(
                                imageVector = getGenreIcon(genre.name),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        modifier = Modifier.clickable { viewModel.selectGenre(genre.name) }
                    )
                }
            }
        }
    }
}

@Composable
private fun getGenreIcon(genreName: String) = when {
    genreName.contains("rock", ignoreCase = true) -> Icons.Default.Album
    genreName.contains("pop", ignoreCase = true) -> Icons.Default.Star
    genreName.contains("jazz", ignoreCase = true) -> Icons.Default.MusicNote
    genreName.contains("classical", ignoreCase = true) -> Icons.Default.Piano
    genreName.contains("hip", ignoreCase = true) || genreName.contains("rap", ignoreCase = true) -> Icons.Default.Headphones
    genreName.contains("electronic", ignoreCase = true) || genreName.contains("edm", ignoreCase = true) -> Icons.Default.GraphicEq
    genreName.contains("country", ignoreCase = true) -> Icons.Default.Nature
    genreName.contains("indie", ignoreCase = true) -> Icons.Default.Subscriptions
    genreName.contains("metal", ignoreCase = true) -> Icons.Default.Album
    genreName.contains("r&b", ignoreCase = true) -> Icons.Default.SurroundSound
    else -> Icons.Default.Category
}

data class GenreInfo(
    val name: String,
    val songCount: Int
)