package com.fourshil.musicya.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.theme.NeoDimens

/**
 * Onboarding flow shown on first launch.
 */
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            icon = Icons.Default.MusicNote,
            title = "Welcome to Musicya",
            description = "Your beautiful offline music player. Let's get started!"
        ),
        OnboardingPage(
            icon = Icons.Default.Folder,
            title = "Grant Music Access",
            description = "Musicya needs permission to access your music files. Your files stay on your device."
        ),
        OnboardingPage(
            icon = Icons.Default.AutoAwesome,
            title = "Smart Features",
            description = "Enjoy smart playlists, sleep timer, crossfade, and more. All offline, all private."
        ),
        OnboardingPage(
            icon = Icons.Default.PlayArrow,
            title = "Ready to Play",
            description = "Your music library is ready. Tap any song to start playing!"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            OnboardingPageContent(pages[page])
        }

        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(NeoDimens.SpacingM),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                val color = if (index == pagerState.currentPage) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(8.dp)
                        .then(
                            Modifier.size(if (index == pagerState.currentPage) 24.dp else 8.dp)
                        )
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = color,
                        modifier = Modifier.fillMaxSize()
                    ) {}
                }
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = NeoDimens.ScreenPadding, vertical = NeoDimens.SpacingM),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (pagerState.currentPage > 0) {
                TextButton(onClick = {
                    // Go to previous page
                }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        // Go to next page
                    } else {
                        onComplete()
                    }
                }
            ) {
                Text(
                    if (pagerState.currentPage < pages.size - 1) "Next" else "Get Started"
                )
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(NeoDimens.ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(NeoDimens.SpacingXL))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(NeoDimens.SpacingM))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class OnboardingPage(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val description: String
)