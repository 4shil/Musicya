package com.fourshil.musicya.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v4.media.MediaStyleHelper
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.fourshil.musicya.MainActivity
import com.fourshil.musicya.R
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.player.PlayerController

/**
 * Advanced notification manager with media style and action buttons.
 */
class MediaNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "musicya_playback"
        const val NOTIFICATION_ID = 1001
        
        const val ACTION_PLAY = "com.fourshil.musicya.PLAY"
        const val ACTION_PAUSE = "com.fourshil.musicya.PAUSE"
        const val ACTION_NEXT = "com.fourshil.musicya.NEXT"
        const val ACTION_PREVIOUS = "com.fourshil.musicya.PREVIOUS"
        const val ACTION_STOP = "com.fourshil.musicya.STOP"
        const val ACTION_TOGGLE_FAVORITE = "com.fourshil.musicya.TOGGLE_FAVORITE"
    }

    private var mediaSession: MediaSessionCompat? = null
    private var playerController: PlayerController? = null

    fun initialize(controller: PlayerController, session: MediaSessionCompat) {
        this.playerController = controller
        this.mediaSession = session
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Now Playing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media playback controls and album art"
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(
        song: Song,
        isPlaying: Boolean,
        albumArt: Bitmap?,
        isFavorite: Boolean,
        playbackSpeed: Float = 1.0f
    ): Notification {
        val contentIntent = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mediaStyle = MediaStyle()
            .setMediaSession(mediaSession?.sessionToken)
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)
            .setCancelButtonIntent(createPendingIntent(ACTION_STOP))

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentTitle(song.title)
            .setContentText(song.artist)
            .setSubText(song.album)
            .setContentIntent(contentIntent)
            .setStyle(mediaStyle)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .addAction(createPlayPauseAction(isPlaying))
            .addAction(createPreviousAction())
            .addAction(createNextAction())
            .addAction(
                if (isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off,
                "Favorite",
                createPendingIntent(ACTION_TOGGLE_FAVORITE)
            )

        // Add album art if available
        if (albumArt != null) {
            builder.setLargeIcon(albumArt)
        }

        // Set playback speed for fast forward/rewind indication
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            builder.setSpeed(playbackSpeed)
        }

        return builder.build()
    }

    private fun createPlayPauseAction(isPlaying: Boolean): NotificationCompat.Action {
        val icon = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play
        val intent = createPendingIntent(if (isPlaying) ACTION_PAUSE else ACTION_PLAY)
        return NotificationCompat.Action.Builder(icon, "Play/Pause", intent).build()
    }

    private fun createPreviousAction(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_previous,
            "Previous",
            createPendingIntent(ACTION_PREVIOUS)
        ).build()
    }

    private fun createNextAction(): NotificationCompat.Action {
        return NotificationCompat.Action.Builder(
            android.R.drawable.ic_media_next,
            "Next",
            createPendingIntent(ACTION_NEXT)
        ).build()
    }

    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(action).apply {
            setPackage(context.packageName)
        }
        return PendingIntent.getBroadcast(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun updateNotification(notification: Notification) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    fun cancelNotification() {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(NOTIFICATION_ID)
    }
}