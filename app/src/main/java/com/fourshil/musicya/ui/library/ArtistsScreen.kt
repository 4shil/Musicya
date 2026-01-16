package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Artist
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

@Composable
fun ArtistsScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onArtistClick: (String) -> Unit = {}
) {
    val artists by viewModel.artists.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header "MUSES"
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "THE",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black
                ),
                modifier = Modifier.align(Alignment.TopStart)
            )
            Text(
                text = "MUSES",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = PureBlack
                ),
                modifier = Modifier.padding(top = 24.dp)
            )
             Text(
                text = "VOICES",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Black,
                    fontStyle = FontStyle.Italic,
                    color = MangaRed.copy(alpha=0.3f) // Shadow text
                ),
                modifier = Modifier.padding(top = 32.dp, start = 8.dp).zIndex(-1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PureBlack)
            }
        } else if (artists.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("NO VOICES", style = MaterialTheme.typography.headlineLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 160.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(artists, key = { it.id }) { artist ->
                    val artistSongs = songs.filter { it.artist == artist.name }
                    
                    ArtistArtisticItem(
                        artist = artist,
                        artUris = artistSongs.map { it.albumArtUri },
                        onClick = { onArtistClick(artist.name) }
                    )
                }
            }
        }
    }
}

@Composable
fun ArtistArtisticItem(
    artist: Artist,
    artUris: List<android.net.Uri>,
    onClick: () -> Unit
) {
    ArtisticCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Art Grid
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(3.dp, PureBlack)
                    .background(PureBlack)
            ) {
                 PlaylistArtGrid(uris = artUris, size = 64.dp)
            }
           
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = artist.name.uppercase(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${artist.songCount} TRKS // ${artist.albumCount} ALBS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = MangaRed
                )
            }
        }
    }
}
