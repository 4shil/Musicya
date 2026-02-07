package com.fourshil.musicya.ui.album

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.NeoPink

/**
 * Album detail screen showing album info and track listing.
 */
@Composable
fun AlbumDetailScreen(
    albumId: Long,
    viewModel: AlbumDetailViewModel = hiltViewModel(),
    onSongClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(albumId) { viewModel.loadAlbum(albumId) }
    
    val album by viewModel.album.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            state = listState
        ) {
            // Album header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                ) {
                    // Album art background blur
                    AsyncImage(
                        model = album?.artUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                                        MaterialTheme.colorScheme.surface
                                    )
                                )
                            )
                    )
                    // Back button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(NeoDimens.SpacingS)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    // Album info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(NeoDimens.ScreenPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Album art
                        Card(
                            modifier = Modifier.size(180.dp),
                            shape = RoundedCornerShape(NeoDimens.CornerMedium),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                        ) {
                            AsyncImage(
                                model = album?.artUri,
                                contentDescription = "Album art",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.height(NeoDimens.SpacingM))
                        Text(
                            text = album?.name ?: "Loading...",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = album?.artist ?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${songs.size} songs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Play all button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(NeoDimens.ScreenPadding),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { songs.firstOrNull()?.let { onSongClick(it.id) } },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(NeoDimens.SpacingS))
                        Text("Play All")
                    }
                    Spacer(modifier = Modifier.width(NeoDimens.SpacingM))
                    OutlinedButton(
                        onClick = { /* TODO: Shuffle play */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null)
                        Spacer(modifier = Modifier.width(NeoDimens.SpacingS))
                        Text("Shuffle")
                    }
                }
            }
            
            // Track list
            items(
                items = songs,
                key = { it.id }
            ) { song ->
                SongListItem(
                    song = song,
                    isFavorite = false, // TODO: Check favorite status
                    isPlaying = false,
                    onClick = { onSongClick(song.id) },
                    onMoreClick = { /* TODO: Song actions */ }
                )
            }
            
            // Bottom spacing
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}