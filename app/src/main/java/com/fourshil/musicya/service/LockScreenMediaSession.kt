package com.fourshil.musicya.service

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.fourshil.musicya.player.PlayerController

/**
 * Manages lock screen media controls via MediaSession.
 */
class LockScreenMediaSession(private val context: Context) {

    private var mediaSession: MediaSessionCompat? = null
    private var playerController: PlayerController? = null

    fun initialize(controller: PlayerController) {
        playerController = controller
        
        mediaSession = MediaSessionCompat(context, "MusicyaMediaSession").apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            
            setCallback(object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    controller.play()
                }

                override fun onPause() {
                    controller.pause()
                }

                override fun onSkipToNext() {
                    controller.skipToNext()
                }

                override fun onSkipToPrevious() {
                    controller.skipToPrevious()
                }

                override fun onStop() {
                    controller.pause()
                }

                override fun onSeekTo(pos: Long) {
                    controller.seekTo(pos)
                }
            })

            isActive = true
        }
    }

    fun updateMetadata(title: String, artist: String, album: String, duration: Long) {
        val metadata = MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            .build()

        mediaSession?.setMetadata(metadata)
    }

    fun updatePlaybackState(isPlaying: Boolean, position: Long) {
        val state = if (isPlaying) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        val playbackState = PlaybackStateCompat.Builder()
            .setState(state, position, 1.0f)
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_STOP
            )
            .build()

        mediaSession?.setPlaybackState(playbackState)
    }

    fun release() {
        mediaSession?.let {
            it.isActive = false
            it.release()
        }
        mediaSession = null
    }
}