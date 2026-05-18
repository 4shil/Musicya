package com.fourshil.musicya.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Multi-select mode bar for batch operations on songs.
 */
@Composable
fun MultiSelectBar(
    selectedCount: Int,
    totalCount: Int,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onAddToPlaylist: () -> Unit,
    onAddToQueue: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NeoDimens.ScreenPadding, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$selectedCount of $totalCount selected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = if (selectedCount == totalCount) onDeselectAll else onSelectAll) {
                    Text(if (selectedCount == totalCount) "Deselect All" else "Select All")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = onAddToPlaylist, enabled = selectedCount > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.PlaylistAdd, contentDescription = "Add to Playlist")
                        Text("Playlist", style = MaterialTheme.typography.labelSmall)
                    }
                }
                IconButton(onClick = onAddToQueue, enabled = selectedCount > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.AddToQueue, contentDescription = "Add to Queue")
                        Text("Queue", style = MaterialTheme.typography.labelSmall)
                    }
                }
                IconButton(onClick = onShare, enabled = selectedCount > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                        Text("Share", style = MaterialTheme.typography.labelSmall)
                    }
                }
                IconButton(onClick = onDelete, enabled = selectedCount > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                        Text("Delete", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

/**
 * Confirmation dialog for batch delete operation.
 */
@Composable
fun BatchDeleteDialog(
    count: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Songs") },
        text = {
            Text(
                "Are you sure you want to delete $count song${if (count > 1) "s" else ""} from the library? " +
                "This action cannot be undone."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * File info dialog showing detailed song metadata.
 */
@Composable
fun FileInfoDialog(
    song: Song,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("File Information") },
        text = {
            LazyColumn {
                item { FileInfoRow("Title", song.title) }
                item { FileInfoRow("Artist", song.artist) }
                item { FileInfoRow("Album", song.album) }
                item { FileInfoRow("Duration", formatDuration(song.duration)) }
                item { FileInfoRow("File Path", song.path) }
                item { FileInfoRow("File Size", formatFileSize(song.fileSize)) }
                item { FileInfoRow("Bitrate", "${song.bitrate} kbps") }
                item { FileInfoRow("Sample Rate", "${song.sampleRate} Hz") }
                item { FileInfoRow("Channels", if (song.channels == 2) "Stereo" else "Mono") }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun FileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp))
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "${minutes}:${seconds.toString().padStart(2, '0')}"
}

private fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
        else -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
    }
}