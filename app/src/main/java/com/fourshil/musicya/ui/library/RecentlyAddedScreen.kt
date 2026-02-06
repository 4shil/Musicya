package com.fourshil.musicya.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.ui.components.SongListItem

/**
 * Recently Added smart playlist - shows songs added in the last 7 days.
 */
@Composable
fun RecentlyAddedScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onSongClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Filter songs added in the last 7 days
    val sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
    val recentlyAddedSongs = remember(songs) {
        songs.filter { it.dateAdded > sevenDaysAgo }
            .sortedByDescending { it.dateAdded }
    }

    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    NeoScaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recently Added") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
        } else if (recentlyAddedSongs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NewReleases,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No recent additions",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Songs added in the last 7 days will appear here",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                state = listState,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 100.dp
                )
            ) {
                item {
                    Text(
                        text = "${recentlyAddedSongs.size} songs added this week",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(
                    count = recentlyAddedSongs.size,
                    key = { index -> recentlyAddedSongs[index].id }
                ) { index ->
                    val song = recentlyAddedSongs[index]
                    SongListItem(
                        song = song,
                        isFavorite = favorites.contains(song.id),
                        isPlaying = false,
                        onClick = { onSongClick(song.id) },
                        onMoreClick = { /* TODO: Show song actions */ }
                    )
                }
            }
        }
    }
}