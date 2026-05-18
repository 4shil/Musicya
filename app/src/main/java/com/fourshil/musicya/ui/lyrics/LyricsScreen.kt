package com.fourshil.musicya.ui.lyrics

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.components.ErrorState
import com.fourshil.musicya.ui.components.NetworkErrorState
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.util.NetworkResult
import com.fourshil.musicya.util.NetworkUtils

/**
 * Lyrics screen with online fetching and offline display.
 */
@Composable
fun LyricsScreen(
    title: String,
    artist: String,
    onBack: () -> Unit
) {
    var lyricsState by remember { mutableStateOf<NetworkResult<String>>(NetworkResult.Loading) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(title, artist) {
        isRefreshing = true
        val result = NetworkUtils.fetchLyrics(title, artist)
        lyricsState = result.fold(
            onSuccess = { NetworkResult.Success(it) },
            onFailure = { NetworkResult.Error(it.message ?: "Failed to fetch lyrics") }
        )
        isRefreshing = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lyrics") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            isRefreshing = true
                            // Trigger re-fetch
                        },
                        enabled = !isRefreshing
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh lyrics")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = lyricsState) {
                is NetworkResult.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is NetworkResult.Success -> {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(NeoDimens.ScreenPadding)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = artist,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = NeoDimens.SpacingL)
                        )
                        Text(
                            text = state.data,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                is NetworkResult.Error -> {
                    NetworkErrorState(
                        onRetry = {
                            lyricsState = NetworkResult.Loading
                            // Trigger re-fetch
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}