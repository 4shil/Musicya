package com.fourshil.musicya.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.fourshil.musicya.player.PlayerController

/**
 * Quick Settings tile for controlling playback from the notification shade.
 * Requires Android 7.0+ (API 24).
 */
@RequiresApi(Build.VERSION_CODES.N)
class MusicyaTileService : TileService() {

    private var playerController: PlayerController? = null

    override fun onCreate() {
        super.onCreate()
        // Get player controller from application
        playerController = (application as? com.fourshil.musicya.MusicyaApplication)
            ?.playerController
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        
        playerController?.let { controller ->
            if (controller.isPlaying.value) {
                controller.pause()
            } else {
                controller.play()
            }
            updateTile()
        }
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        val controller = playerController ?: return

        tile.apply {
            state = if (controller.isPlaying.value) {
                Tile.STATE_ACTIVE
            } else {
                Tile.STATE_INACTIVE
            }
            
            label = controller.currentMediaItem?.value?.title ?: "Musicya"
            contentDescription = if (controller.isPlaying.value) "Pause playback" else "Play music"
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                subtitle = controller.currentMediaItem?.value?.artist
            }
            
            updateTile()
        }
    }
}

/**
 * Gesture handler for swipe-based navigation and controls.
 */
object GestureHandler {

    /**
     * Swipe directions for gesture recognition.
     */
    enum class SwipeDirection {
        LEFT, RIGHT, UP, DOWN
    }

    /**
     * Gesture actions that can be triggered.
     */
    enum class GestureAction {
        NEXT_TRACK,
        PREVIOUS_TRACK,
        VOLUME_UP,
        VOLUME_DOWN,
        SEEK_FORWARD,
        SEEK_BACKWARD,
        EXPAND_PLAYER,
        COLLAPSE_PLAYER,
        NONE
    }

    /**
     * Map swipe gestures to actions based on context.
     */
    fun mapGestureToAction(
        direction: SwipeDirection,
    context: GestureContext
    ): GestureAction {
        return when (context) {
            GestureContext.PLAYER_SCREEN -> when (direction) {
                SwipeDirection.LEFT -> GestureAction.NEXT_TRACK
                SwipeDirection.RIGHT -> GestureAction.PREVIOUS_TRACK
                SwipeDirection.UP -> GestureAction.EXPAND_PLAYER
                SwipeDirection.DOWN -> GestureAction.COLLAPSE_PLAYER
            }
            GestureContext.MINI_PLAYER -> when (direction) {
                SwipeDirection.LEFT -> GestureAction.NEXT_TRACK
                SwipeDirection.RIGHT -> GestureAction.PREVIOUS_TRACK
                SwipeDirection.UP -> GestureAction.EXPAND_PLAYER
                SwipeDirection.DOWN -> GestureAction.NONE
            }
            GestureContext.PLAYLIST_SCREEN -> when (direction) {
                SwipeDirection.LEFT -> GestureAction.NEXT_TRACK
                SwipeDirection.RIGHT -> GestureAction.PREVIOUS_TRACK
                else -> GestureAction.NONE
            }
            GestureContext.SONG_LIST -> when (direction) {
                SwipeDirection.LEFT -> GestureAction.NONE // Could be delete
                SwipeDirection.RIGHT -> GestureAction.NONE // Could be add to queue
                else -> GestureAction.NONE
            }
        }
    }

    enum class GestureContext {
        PLAYER_SCREEN,
        MINI_PLAYER,
        PLAYLIST_SCREEN,
        SONG_LIST
    }
}