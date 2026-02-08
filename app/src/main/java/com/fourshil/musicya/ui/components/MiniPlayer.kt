package com.fourshil.musicya.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Enhanced mini player with swipe gestures and quick actions.
 */
@Composable
fun MiniPlayer(
    song: Song,
    isPlaying: Boolean,
    progress: Float, // 0f to 1f
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onExpand: () -> Unit,
    onQuickAction: (QuickAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickActions by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column {
            // Progress bar at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpand)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Album art with gradient overlay
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    // Album art would go here
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Song info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Quick action toggle
                IconButton(onClick = { showQuickActions = !showQuickActions }) {
                    Icon(
                        if (showQuickActions) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Quick actions"
                    )
                }

                // Play/Pause button
                FilledIconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }

                // Skip next
                IconButton(onClick = onSkipNext, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next"
                    )
                }
            }

            // Quick actions panel
            AnimatedVisibility(
                visible = showQuickActions,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                QuickActionsPanel(
                    currentSong = song,
                    onAction = { action ->
                        onQuickAction(action)
                        showQuickActions = false
                    }
                )
            }
        }
    }
}

/**
 * Quick actions panel with common playback actions.
 */
@Composable
private fun QuickActionsPanel(
    currentSong: Song,
    onAction: (QuickAction) -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Default.Shuffle,
                label = "Shuffle",
                onClick = { onAction(QuickAction.Shuffle) }
            )
            QuickActionButton(
                icon = Icons.Default.Repeat,
                label = "Repeat",
                onClick = { onAction(QuickAction.Repeat) }
            )
            QuickActionButton(
                icon = Icons.Default.Favorite,
                label = "Favorite",
                onClick = { onAction(QuickAction.ToggleFavorite) }
            )
            QuickActionButton(
                icon = Icons.Default.PlaylistAdd,
                label = "Playlist",
                onClick = { onAction(QuickAction.AddToPlaylist) }
            )
            QuickActionButton(
                icon = Icons.Default.Share,
                label = "Share",
                onClick = { onAction(QuickAction.Share) }
            )
            QuickActionButton(
                icon = Icons.Default.Info,
                label = "Info",
                onClick = { onAction(QuickAction.ShowInfo) }
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Quick actions enum.
 */
enum class QuickAction {
    Shuffle,
    Repeat,
    ToggleFavorite,
    AddToPlaylist,
    Share,
    ShowInfo,
    PlayNext,
    AddToQueue
}