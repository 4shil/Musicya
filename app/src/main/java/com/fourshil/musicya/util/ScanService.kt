package com.fourshil.musicya.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.fourshil.musicya.MainActivity
import com.fourshil.musicya.R

/**
 * Foreground service for scanning music library with progress notifications.
 */
class ScanService : Service() {

    companion object {
        const val CHANNEL_ID = "scan_channel"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START_SCAN = "com.fourshil.musicya.START_SCAN"
        const val ACTION_STOP_SCAN = "com.fourshil.musicya.STOP_SCAN"

        fun startScan(context: Context) {
            val intent = Intent(context, ScanService::class.java).apply {
                action = ACTION_START_SCAN
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopScan(context: Context) {
            val intent = Intent(context, ScanService::class.java).apply {
                action = ACTION_STOP_SCAN
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_SCAN -> startForegroundScanning()
            ACTION_STOP_SCAN -> stopSelf()
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Scanning",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress when scanning your music library"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startForegroundScanning() {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Scanning Music")
            .setContentText("Looking for music files...")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setProgress(0, 0, true)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    /**
     * Update the scan progress notification.
     */
    fun updateProgress(current: Int, total: Int, message: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Scanning Music")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setProgress(total, current, false)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Complete the scan and show completion notification.
     */
    fun completeScan(songCount: Int) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Scan Complete")
            .setContentText("Found $songCount songs")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
        
        stopForeground(STOP_FOREGROUND_DETACH)
        stopSelf()
    }
}