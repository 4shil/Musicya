package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.fourshil.musicya.ui.components.ArtisticButton
import com.fourshil.musicya.ui.components.ArtisticCard
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate700
import com.fourshil.musicya.ui.theme.Slate900

@Composable
fun PermissionRequiredView(onRequest: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LOCKED", style = MaterialTheme.typography.displayMedium)
             Spacer(modifier = Modifier.height(16.dp))
            ArtisticButton(
                text = "ACCESS DATA",
                onClick = onRequest
            )
        }
    }
}

@Composable
fun SelectionTopBar(
    selectedCount: Int,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onActions: () -> Unit
) {
    // Selection mode top bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ArtisticCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Slate900,
            borderColor = Slate700
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, null, tint = Slate50)
                }
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Slate50
                )
                Row {
                    IconButton(onClick = onSelectAll) {
                        Icon(Icons.Default.SelectAll, null, tint = Slate50)
                    }
                    IconButton(onClick = onActions) {
                        Icon(Icons.Default.MoreVert, null, tint = Slate50)
                    }
                }
            }
        }
    }
}

@Composable
fun SongsScreenHeader(
    onMenuClick: () -> Unit,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(24.dp))
    // Header: STUDIO FEED
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Musicya",
            style = MaterialTheme.typography.displayMedium.copy(
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        ArtisticButton(
            onClick = onMenuClick,
            icon = { Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.onSurface) },
            modifier = Modifier.size(52.dp)
        )
    }
    
    Spacer(modifier = Modifier.height(24.dp))
    
    // Search Bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(NeoDimens.BorderThin, MaterialTheme.colorScheme.outline)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = NeoCoral,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Search songs...",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
    Spacer(modifier = Modifier.height(24.dp))

    TopNavigationChips(
        items = listOf(
            TopNavItem(com.fourshil.musicya.ui.navigation.Screen.Songs.route, "Songs"),
            TopNavItem(com.fourshil.musicya.ui.navigation.Screen.RecentlyPlayed.route, "Recent"),
            TopNavItem(com.fourshil.musicya.ui.navigation.Screen.MostPlayed.route, "Top"),
            TopNavItem(com.fourshil.musicya.ui.navigation.Screen.Favorites.route, "Favorites"),
            TopNavItem(com.fourshil.musicya.ui.navigation.Screen.Playlists.route, "Playlists"),
            TopNavItem(com.fourshil.musicya.ui.navigation.Screen.Albums.route, "Albums")
        ),
        currentRoute = currentRoute,
        onItemClick = onNavigate,
        modifier = Modifier.padding(bottom = 24.dp)
    )
}
