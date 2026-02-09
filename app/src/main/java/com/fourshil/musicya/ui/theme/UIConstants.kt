package com.fourshil.musicya.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * UI constants for consistent styling across the app.
 */
object UIConstants {

    // Animation durations
    const val ANIMATION_DURATION_SHORT = 150L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L
    
    // Touch target sizes (Material Design guidelines)
    const val MIN_TOUCH_TARGET_SIZE = 48
    const val LARGE_TOUCH_TARGET_SIZE = 56
    
    // Padding
    const val PADDING_TINY = 4
    const val PADDING_SMALL = 8
    const val PADDING_MEDIUM = 16
    const val PADDING_LARGE = 24
    const val PADDING_XLARGE = 32
    
    // Car mode sizes
    const val CAR_MODE_BUTTON_SIZE = 96
    const val CAR_MODE_ICON_SIZE = 64
    
    // Widget sizes
    const val WIDGET_WIDTH = 4
    const val WIDGET_HEIGHT = 2
    
    // Mini player
    const val MINI_PLAYER_HEIGHT = 64
    const val MINI_PLAYER_ALBUM_ART_SIZE = 48
    
    // Now playing
    const val NOW_PLAYING_ALBUM_ART_SIZE = 300
    
    // Search
    const val SEARCH_DEBOUNCE_MS = 150L
    const val MAX_RECENT_SEARCHES = 10
    
    // Queue
    const val DRAG_HANDLE_SIZE = 40
    
    // Equalizer
    const val EQUALIZER_BAND_COUNT = 8
    const val EQ_FREQUENCY_BASE = 32
    const val EQ_FREQUENCY_MULTIPLIER = 1.5
    
    // File sizes
    const val MAX_ALBUM_ART_CACHE_SIZE = 50 * 1024 * 1024L // 50MB
    const val MAX_SONG_CACHE_SIZE = 100 * 1024 * 1024L // 100MB
}

/**
 * Neo-Brutalist design colors for Musicya.
 */
object NeoColors {
    // Primary palette
    val Primary = Color(0xFFD97757)
    val PrimaryDark = Color(0xFFB5593A)
    val PrimaryLight = Color(0xFFE89A7A)
    
    // Secondary palette  
    val Secondary = Color(0xFF1E88E5)
    val SecondaryDark = Color(0xFF1565C0)
    val SecondaryLight = Color(0xFF42A5F5)
    
    // Background colors
    val BackgroundLight = Color(0xFFFFFBFE)
    val BackgroundDark = Color(0xFF1C1B1F)
    val SurfaceLight = Color(0xFFF5F5F5)
    val SurfaceDark = Color(0xFF2D2D2D)
    
    // Text colors
    val OnPrimary = Color.White
    val OnSecondary = Color.White
    val OnBackgroundLight = Color(0xFF1C1B1F)
    val OnBackgroundDark = Color(0xFFE6E1E5)
    
    // Accent colors for UI elements
    val Success = Color(0xFF43A047)
    val Warning = Color(0xFFFFB300)
    val Error = Color(0xFFE53935)
    val Info = Color(0xFF1E88E5)
    
    // Neo-brutalist borders
    val BorderLight = Color(0xFF000000)
    val BorderDark = Color(0xFFE0E0E0)
    
    // Player colors
    val ProgressTrack = Color(0xFFE0E0E0)
    val ProgressActive = Primary
    
    // Shimmer colors
    val ShimmerBase = Color(0xFFE0E0E0)
    val ShimmerHighlight = Color(0xFFF5F5F5)
}