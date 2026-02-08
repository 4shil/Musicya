package com.fourshil.musicya.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.ui.components.SongListItem
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.util.PlaybackStats
import com.fourshil.musicya.util.QueueUtils

/**
 * Statistics screen showing listening history and stats.
 */
@Composable
fun StatisticsScreen(
    totalSongs: Int,
    totalDuration: Long,
    recentlyPlayed: List<Song>,
    mostPlayed: List<Song>,
    onSongClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Most Played", "Recently Played")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> OverviewTab(totalSongs, totalDuration)
                1 -> SongListTab(mostPlayed, "Top Played Songs", onSongClick)
                2 -> SongListTab(recentlyPlayed, "Recently Played", onSongClick)
            }
        }
    }
}

@Composable
private fun OverviewTab(totalSongs: Int, totalDuration: Long) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(NeoDimens.ScreenPadding)
    ) {
        item {
            Text(
                text = "Library Overview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            StatCard(
                icon = Icons.Default.MusicNote,
                title = "Total Songs",
                value = "$totalSongs",
                subtitle = "songs in library"
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                icon = Icons.Default.Timer,
                title = "Total Duration",
                value = QueueUtils.formatDuration(totalDuration),
                subtitle = "of music"
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                icon = Icons.Default.PlayCircle,
                title = "Songs Played",
                value = "${PlaybackStats.totalSongsPlayed.value}",
                subtitle = "total plays"
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                icon = Icons.Default.Schedule,
                title = "Listening Time",
                value = QueueUtils.formatDuration(PlaybackStats.totalListeningTime.value),
                subtitle = "total time listened"
            )
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                icon = Icons.Default.DateRange,
                title = "Last Played",
                value = formatLastPlayed(PlaybackStats.lastPlayedTimestamp.value),
                subtitle = "most recent play"
            )
        }
    }
}

@Composable
private fun StatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SongListTab(
    songs: List<Song>,
    title: String,
    onSongClick: (Long) -> Unit
) {
    if (songs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
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
        }
    }
}

private fun formatLastPlayed(timestamp: Long): String {
    if (timestamp == 0L) return "Never"
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000} min ago"
        diff < 86400_000 -> "${diff / 3600_000} hours ago"
        diff < 604800_000 -> "${diff / 86400_000} days ago"
        else -> "${diff / 604800_000} weeks ago"
    }
}