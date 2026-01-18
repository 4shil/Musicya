package com.fourshil.musicya.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.util.Lyrics
import com.fourshil.musicya.util.LyricsParser
import kotlinx.coroutines.launch
import java.io.File

/**
 * Neo-Brutalism Lyrics Display Bottom Sheet
 * Displays synced LRC lyrics with auto-scroll functionality
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsBottomSheet(
    songPath: String,
    currentPositionMs: Long,
    onDismiss: () -> Unit
) {
    val lyrics = remember(songPath) {
        loadLyricsForSong(songPath)
    }
    
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    
    val currentLineIndex = if (lyrics != null && lyrics.lines.isNotEmpty()) {
        lyrics.getCurrentLineIndex(currentPositionMs)
    } else -1
    
    // Auto-scroll to current line
    LaunchedEffect(currentLineIndex) {
        if (currentLineIndex >= 0) {
            scope.launch {
                listState.animateScrollToItem(
                    index = currentLineIndex,
                    scrollOffset = -200 // Offset to center the current line
                )
            }
        }
    }
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .padding(NeoDimens.ScreenPadding)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lyrics",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 2.dp,
                color = MaterialTheme.colorScheme.outline
            )
            
            if (lyrics == null || lyrics.lines.isEmpty()) {
                // No lyrics found
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No lyrics found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Place a .lrc file next to the audio file",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                // Display lyrics
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 48.dp)
                ) {
                    itemsIndexed(
                        items = lyrics.lines,
                        key = { index, line -> "${index}_${line.timeMs}" }
                    ) { index, line ->
                        val isCurrentLine = index == currentLineIndex
                        
                        Text(
                            text = line.text,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = if (isCurrentLine) 20.sp else 16.sp,
                                fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal,
                                lineHeight = 28.sp
                            ),
                            color = if (isCurrentLine) {
                                NeoCoral
                            } else {
                                MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = if (index < currentLineIndex) 0.4f else 0.7f
                                )
                            },
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Load lyrics from .lrc file adjacent to the audio file
 */
private fun loadLyricsForSong(songPath: String): Lyrics? {
    if (songPath.isBlank()) return null
    
    val audioFile = File(songPath)
    if (!audioFile.exists()) return null
    
    // Try common LRC file patterns
    val baseName = audioFile.nameWithoutExtension
    val parentDir = audioFile.parentFile ?: return null
    
    val lrcPatterns = listOf(
        File(parentDir, "$baseName.lrc"),
        File(parentDir, "$baseName.LRC"),
        File(parentDir, "${audioFile.name}.lrc")
    )
    
    val lrcFile = lrcPatterns.firstOrNull { it.exists() && it.canRead() }
    
    return lrcFile?.let { LyricsParser.parseFile(it) }
}
