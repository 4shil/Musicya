package com.fourshil.musicya.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.fourshil.musicya.data.model.Song

/**
 * Deep link handler for handling external URL schemes.
 */
object DeepLinkHandler {

    // URI Schemes
    const val SCHEME_MUSICYA = "musicya"
    const val SCHEME_HTTP = "http"
    const val SCHEME_HTTPS = "https"

    // Host patterns
    const val HOST_PLAY = "play"
    const val HOST_QUEUE = "queue"
    const val HOST_PLAYLIST = "playlist"
    const val HOST_ALBUM = "album"
    const val HOST_ARTIST = "artist"

    /**
     * Handle incoming deep link intent.
     */
    fun handleIntent(intent: Intent?): DeepLinkResult {
        if (intent == null) return DeepLinkResult.Unknown

        val action = intent.action
        if (action == Intent.ACTION_VIEW) {
            return handleUri(intent.data)
        }

        // Handle internal intents
        when (action) {
            "com.fourshil.musicya.SHUFFLE_ALL" -> return DeepLinkResult.ShuffleAll
            "com.fourshil.musicya.RECENTLY_PLAYED" -> return DeepLinkResult.RecentlyPlayed
            "com.fourshil.musicya.FAVORITES" -> return DeepLinkResult.Favorites
            "com.fourshil.musicya.RANDOM" -> return DeepLinkResult.Random
        }

        return DeepLinkResult.Unknown
    }

    /**
     * Handle URI for deep linking.
     * Format: musicya://play?songId=123
     * Format: musicya://playlist?id=456
     */
    private fun handleUri(uri: Uri?): DeepLinkResult {
        if (uri == null) return DeepLinkResult.Unknown

        return when (uri.host) {
            HOST_PLAY -> {
                val songId = uri.getQueryParameter("songId")?.toLongOrNull()
                val searchQuery = uri.getQueryParameter("q")
                when {
                    songId != null -> DeepLinkResult.PlaySong(songId)
                    !searchQuery.isNullOrBlank() -> DeepLinkResult.Search(searchQuery)
                    else -> DeepLinkResult.Unknown
                }
            }
            HOST_QUEUE -> {
                val songIds = uri.getQueryParameter("ids")
                    ?.split(",")
                    ?.mapNotNull { it.toLongOrNull() }
                if (!songIds.isNullOrEmpty()) {
                    DeepLinkResult.PlayQueue(songIds)
                } else DeepLinkResult.Unknown
            }
            HOST_PLAYLIST -> {
                val playlistId = uri.getQueryParameter("id")?.toLongOrNull()
                if (playlistId != null) DeepLinkResult.OpenPlaylist(playlistId)
                else DeepLinkResult.Unknown
            }
            HOST_ALBUM -> {
                val albumId = uri.getQueryParameter("id")?.toLongOrNull()
                if (albumId != null) DeepLinkResult.OpenAlbum(albumId)
                else DeepLinkResult.Unknown
            }
            HOST_ARTIST -> {
                val artistName = uri.getQueryParameter("name")
                if (!artistName.isNullOrBlank()) DeepLinkResult.OpenArtist(artistName)
                else DeepLinkResult.Unknown
            }
            else -> DeepLinkResult.Unknown
        }
    }

    /**
     * Build a deep link URI for sharing.
     */
    fun buildShareUri(song: Song): Uri {
        return Uri.Builder()
            .scheme(SCHEME_MUSICYA)
            .authority(HOST_PLAY)
            .appendQueryParameter("songId", song.id.toString())
            .build()
    }

    /**
     * Build a search deep link URI.
     */
    fun buildSearchUri(query: String): Uri {
        return Uri.Builder()
            .scheme(SCHEME_MUSICYA)
            .authority(HOST_PLAY)
            .appendQueryParameter("q", query)
            .build()
    }

    /**
     * Build a playlist deep link URI.
     */
    fun buildPlaylistUri(playlistId: Long): Uri {
        return Uri.Builder()
            .scheme(SCHEME_MUSICYA)
            .authority(HOST_PLAYLIST)
            .appendQueryParameter("id", playlistId.toString())
            .build()
    }
}

/**
 * Deep link result types.
 */
sealed class DeepLinkResult {
    data object Unknown : DeepLinkResult()
    data object ShuffleAll : DeepLinkResult()
    data object RecentlyPlayed : DeepLinkResult()
    data object Favorites : DeepLinkResult()
    data object Random : DeepLinkResult()
    data class PlaySong(val songId: Long) : DeepLinkResult()
    data class Search(val query: String) : DeepLinkResult()
    data class PlayQueue(val songIds: List<Long>) : DeepLinkResult()
    data class OpenPlaylist(val playlistId: Long) : DeepLinkResult()
    data class OpenAlbum(val albumId: Long) : DeepLinkResult()
    data class OpenArtist(val artistName: String) : DeepLinkResult()
}

/**
 * Theme customization preferences and manager.
 */
object ThemeManager {

    private const val PREF_NAME = "theme_prefs"
    private const val KEY_PRIMARY_COLOR = "primary_color"
    private const val KEY_ACCENT_COLOR = "accent_color"
    private const val KEY_USE_SYSTEM_THEME = "use_system_theme"
    private const val KEY_DARK_MODE = "dark_mode"
    private const val KEY_DYNAMIC_COLORS = "dynamic_colors"
    private const val KEY_CORNER_RADIUS = "corner_radius"
    private const val KEY_ANIMATION_ENABLED = "animation_enabled"

    // Predefined color options
    val PRIMARY_COLORS = listOf(
        ColorOption("Claude Orange", 0xFFD97757),
        ColorOption("Ocean Blue", 0xFF1E88E5),
        ColorOption("Forest Green", 0xFF43A047),
        ColorOption("Royal Purple", 0xFF7E57C2),
        ColorOption("Rose Pink", 0xFFE91E63),
        ColorOption("Amber Gold", 0xFFFFB300),
        ColorOption("Teal", 0xFF26A69A),
        ColorOption("Deep Red", 0xFFE53935)
    )

    data class ColorOption(val name: String, val color: Long)

    /**
     * Save theme preference.
     */
    fun saveThemePreference(context: Context, key: String, value: Any) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        
        when (value) {
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
            is String -> editor.putString(key, value)
            is Float -> editor.putFloat(key, value)
        }
        editor.apply()
    }

    /**
     * Get theme preference.
     */
    fun getThemePreference(context: Context, key: String, default: Any): Any {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        
        return when (default) {
            is Int -> prefs.getInt(key, default)
            is Boolean -> prefs.getBoolean(key, default)
            is Long -> prefs.getLong(key, default)
            is String -> prefs.getString(key, default) ?: default
            is Float -> prefs.getFloat(key, default)
            else -> default
        }
    }

    /**
     * Apply theme colors to the app.
     */
    fun applyTheme(
        context: Context,
        primaryColor: Long,
        accentColor: Long,
        cornerRadius: Int,
        useAnimations: Boolean
    ) {
        // Store in app state for Compose theme to pick up
        ThemeState.primaryColor = primaryColor
        ThemeState.accentColor = accentColor
        ThemeState.cornerRadius = cornerRadius
        ThemeState.useAnimations = useAnimations
    }
}

/**
 * Runtime theme state holder.
 */
object ThemeState {
    var primaryColor: Long = 0xFFD97757
    var accentColor: Long = 0xFF1E88E5
    var cornerRadius: Int = 12
    var useAnimations: Boolean = true
}