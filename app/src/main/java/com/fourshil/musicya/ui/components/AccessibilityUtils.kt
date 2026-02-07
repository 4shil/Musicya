package com.fourshil.musicya.ui.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * Accessibility and gesture utilities.
 */
object AccessibilityUtils {

    /**
     * Semantic description for a song item.
     */
    fun songSemantics(
        title: String,
        artist: String,
        album: String,
        duration: String,
        isPlaying: Boolean = false
    ): SemanticsPropertyReceiver.() -> Unit = {
        contentDescription = "$title by $artist, album $album, duration $duration" +
            if (isPlaying) ", currently playing" else ""
        if (isPlaying) {
            stateDescription = "Playing"
        }
    }

    /**
     * Semantic description for a playlist item.
     */
    fun playlistSemantics(
        name: String,
        songCount: Int,
        description: String = ""
    ): SemanticsPropertyReceiver.() -> Unit = {
        contentDescription = "$name, $songCount songs" +
            if (description.isNotEmpty()) ", $description" else ""
    }

    /**
     * Semantic description for player controls.
     */
    fun playerControlSemantics(
        action: String,
        isEnabled: Boolean = true
    ): SemanticsPropertyReceiver.() -> Unit = {
        contentDescription = action
        if (!isEnabled) {
            stateDescription = "Disabled"
        }
    }

    /**
     * Custom action labels for TalkBack.
     */
    fun songActionsSemantics(
        onPlay: String = "Play",
        onAddToQueue: String = "Add to queue",
        onAddToPlaylist: String = "Add to playlist",
        onShare: String = "Share",
        onDelete: String = "Delete"
    ): Map<String, String> = mapOf(
        "play" to onPlay,
        "queue" to onAddToQueue,
        "playlist" to onAddToPlaylist,
        "share" to onShare,
        "delete" to onDelete
    )
}

/**
 * Swipe gesture detector for tab navigation.
 */
@Composable
fun SwipeableTabContainer(
    tabCount: Int,
    currentTab: Int,
    onTabChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val swipeThreshold = with(density) { 50.dp.toPx() }

    Box(
        modifier = modifier
            .pointerInput(tabCount, currentTab) {
                detectHorizontalDragGestures(
                    onDragStart = { dragOffset = 0f },
                    onDragEnd = {
                        if (abs(dragOffset) > swipeThreshold) {
                            when {
                                dragOffset > 0 && currentTab > 0 -> onTabChange(currentTab - 1)
                                dragOffset < 0 && currentTab < tabCount - 1 -> onTabChange(currentTab + 1)
                            }
                        }
                    },
                    onDragCancel = { dragOffset = 0f },
                    onHorizontalDrag = { _, dragAmount ->
                        dragOffset += dragAmount
                    }
                )
            }
    ) {
        content()
    }
}