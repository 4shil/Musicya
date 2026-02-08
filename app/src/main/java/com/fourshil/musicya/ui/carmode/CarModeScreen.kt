package com.fourshil.musicya.ui.carmode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.player.PlayerController

/**
 * Car Mode UI - simplified, large touch targets for safe driving.
 * Features extra-large buttons, high contrast, and minimal distractions.
 */
@Composable
fun CarModeScreen(
    currentSong: Song?,
    isPlaying: Boolean,
    queue: List<Song>,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onExit: () -> Unit,
    onQueueItemClick: (Int) -> Unit
) {
    var showQueue by remember { mutableStateOf(false) }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top bar with exit button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExit) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit Car Mode",
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = "CAR MODE",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { showQueue = !showQueue }) {
                    Icon(
                        Icons.Default.QueueMusic,
                        contentDescription = "Queue",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Now playing info - extra large for visibility
            if (currentSong != null) {
                Text(
                    text = currentSong.title,
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = currentSong.artist,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(48.dp))
            } else {
                Text(
                    text = "No music playing",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(48.dp))
            }

            // Large playback controls - minimum 80dp touch targets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous button
                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(64.dp)
                    )
                }

                // Play/Pause button - largest
                FilledIconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(120.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(80.dp)
                    )
                }

                // Next button
                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier.size(80.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quick queue access
            if (queue.isNotEmpty()) {
                Text(
                    text = "Up Next: ${queue.firstOrNull()?.title ?: "Empty"}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Queue overlay
        if (showQueue) {
            AlertDialog(
                onDismissRequest = { showQueue = false },
                title = { Text("Queue") },
                text = {
                    LazyColumn {
                        items(queue.take(10)) { song ->
                            ListItem(
                                headlineContent = { Text(song.title) },
                                supportingContent = { Text(song.artist) },
                                modifier = Modifier.clickable {
                                    onQueueItemClick(queue.indexOf(song))
                                    showQueue = false
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showQueue = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}