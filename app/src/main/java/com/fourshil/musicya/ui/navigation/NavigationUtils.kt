package com.fourshil.musicya.ui.navigation

import com.fourshil.musicya.ui.components.TopNavItem

object NavigationUtils {
    val LibraryTabs = listOf(
        TopNavItem(Screen.Songs.route, "Songs"),
        TopNavItem(Screen.RecentlyPlayed.route, "Recent"),
        TopNavItem(Screen.MostPlayed.route, "Top"),
        TopNavItem(Screen.NeverPlayed.route, "Never"),
        TopNavItem(Screen.Favorites.route, "Favorites"),
        TopNavItem(Screen.Playlists.route, "Playlists"),
        TopNavItem(Screen.Albums.route, "Albums"),
        TopNavItem(Screen.Artists.route, "Artists"),
        TopNavItem(Screen.Folders.route, "Folders")
    )
}
