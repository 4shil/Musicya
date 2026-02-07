package com.fourshil.musicya.ui.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.fourshil.musicya.data.model.Album
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Artist detail screen showing discography and all songs.
 */
@Composable
fun ArtistDetailScreen(
    artistName: String,
    viewModel: ArtistDetailViewModel = hiltViewModel(),
    onSongClick: (Long) -> Unit,
    onAlbumClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    LaunchedEffect(artistName) { viewModel.loadArtist(artistName) }
    
    val albums by viewModel.albums.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            state = listState
        ) {
            // Artist header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    // Gradient background
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primaryContainer,
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
                            contentDescription = "Back"
                        )
                    }
                    // Artist info
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(NeoDimens.ScreenPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Artist avatar placeholder
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(NeoDimens.SpacingM))
                        Text(
                            text = artistName,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${albums.size} albums • ${songs.size} songs",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Albums section
            if (albums.isNotEmpty()) {
                item {
                    Text(
                        text = "Albums",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(NeoDimens.ScreenPadding)
                    )
                }
                items(albums) { album ->
                    ListItem(
                        headlineContent = { Text(album.name) },
                        supportingContent = { Text("${album.songCount} songs") },
                        leadingContent = {
                            Card(
                                modifier = Modifier.size(56.dp)
                            ) {
                                AsyncImage(
                                    model = album.artUri,
                                    contentDescription = album.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        },
                        modifier = Modifier.clickable { onAlbumClick(album.id) }
                    )
                }
            }
            
            // All songs section
            item {
                Text(
                    text = "All Songs",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(NeoDimens.ScreenPadding)
                )
            }
            items(songs, key = { it.id }) { song ->
                SongListItem(
                    song = song,
                    isFavorite = false,
                    isPlaying = false,
                    onClick = { onSongClick(song.id) },
                    onMoreClick = { }
                )
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}