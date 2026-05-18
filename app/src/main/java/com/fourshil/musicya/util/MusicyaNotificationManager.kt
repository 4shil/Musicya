package com.fourshil.musicya.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fourshil.musicya.MainActivity
import com.fourshil.musicya.R

/**
 * Notification manager for playback and library notifications.
 */
object NotificationManager {

    private const val PLAYBACK_CHANNEL_ID = "playback_channel"
    private const val PLAYBACK_NOTIFICATION_ID = 1
    private const val INFO_CHANNEL_ID = "info_channel"
    private const val INFO_NOTIFICATION_ID = 2

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)

            // Playback channel
            val playbackChannel = NotificationChannel(
                PLAYBACK_CHANNEL_ID,
                "Now Playing",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media playback controls"
                setShowBadge(false)
            }

            // Info channel
            val infoChannel = NotificationChannel(
                INFO_CHANNEL_ID,
                "Information",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "App notifications and alerts"
            }

            notificationManager.createNotificationChannels(
                listOf(playbackChannel, infoChannel)
            )
        }
    }

    /**
     * Show a simple info notification.
     */
    fun showInfoNotification(
        context: Context,
        title: String,
        message: String,
        notificationId: Int = INFO_NOTIFICATION_ID
    ) {
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, INFO_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }

    /**
     * Cancel a notification by ID.
     */
    fun cancelNotification(context: Context, notificationId: Int) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancel(notificationId)
    }

    /**
     * Cancel all notifications.
     */
    fun cancelAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.cancelAll()
    }
}