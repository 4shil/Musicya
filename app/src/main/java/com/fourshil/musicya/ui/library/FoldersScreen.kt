package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fourshil.musicya.data.model.Folder
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.components.PlaylistArtGrid
import com.fourshil.musicya.ui.theme.MangaRed
import com.fourshil.musicya.ui.theme.PureBlack

@Composable
fun FoldersScreen(
    viewModel: LibraryViewModel = hiltViewModel(),
    onFolderClick: (String) -> Unit = {}
) {
    val folders by viewModel.folders.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .statusBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(24.dp))
         // Header
        Text(
            text = "FILE",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Black
            )
        )
        Text(
            text = "SYSTEM",
             style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                fontStyle = FontStyle.Italic,
                color = MangaRed
            ),
             modifier = Modifier.padding(bottom = 32.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PureBlack)
            }
        } else if (folders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("EMPTY DRIVE", style = MaterialTheme.typography.headlineLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 160.dp),
                 verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(folders, key = { it.path }) { folder ->
                    // Find songs in this folder for the artwork grid
                    val folderSongs = songs.filter { 
                        val parent = java.io.File(it.path).parent ?: ""
                        parent == folder.path 
                    }
                    
                    FolderArtisticItem(
                        folder = folder,
                        artUris = folderSongs.map { it.albumArtUri },
                        onClick = { onFolderClick(folder.path) }
                    )
                }
            }
        }
    }
}

@Composable
fun FolderArtisticItem(
    folder: Folder,
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
             Box(
                modifier = Modifier.size(56.dp).background(Color.Transparent),
                 contentAlignment = Alignment.Center
             ) {
                 if (artUris.isNotEmpty()) {
                     PlaylistArtGrid(uris = artUris, size = 56.dp)
                 } else {
                     Icon(Icons.Default.Folder, null, tint = PureBlack, modifier = Modifier.size(32.dp))
                 }
             }

             Spacer(modifier = Modifier.width(16.dp))

             Column(modifier = Modifier.weight(1f)) {
                 Text(
                    text = folder.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                    maxLines = 1
                 )
                 Text(
                    text = "${folder.songCount} FILES",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = PureBlack.copy(alpha=0.6f)
                 )
             }
             
             Icon(Icons.Default.ChevronRight, null, tint = PureBlack)
        }
    }
}
