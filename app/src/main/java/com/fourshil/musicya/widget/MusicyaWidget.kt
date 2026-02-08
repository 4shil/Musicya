package com.fourshil.musicya.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.fourshil.musicya.MainActivity
import com.fourshil.musicya.R
import com.fourshil.musicya.player.PlayerController
import com.fourshil.musicya.util.AlbumArtHelper

/**
 * Home screen widget for quick playback controls and now playing info.
 * Supports 4x2 widget layout with album art, song info, and playback controls.
 */
class MusicyaWidget : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.fourshil.musicya.PLAY_PAUSE"
        const val ACTION_NEXT = "com.fourshil.musicya.NEXT"
        const val ACTION_PREVIOUS = "com.fourshil.musicya.PREVIOUS"
        
        private var playerController: PlayerController? = null

        fun updateWidget(context: Context) {
            val intent = Intent(context, MusicyaWidget::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            context.sendBroadcast(intent)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                playerController?.let { controller ->
                    if (controller.isPlaying.value) controller.pause() else controller.play()
                }
            }
            ACTION_NEXT -> playerController?.skipToNext()
            ACTION_PREVIOUS -> playerController?.skipToPrevious()
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_musicya)
        
        // Set click intents for main widget area and controls
        val mainIntent = Intent(context, MainActivity::class.java)
        val mainPendingIntent = PendingIntent.getActivity(
            context, 0, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, mainPendingIntent)

        // Play/Pause button
        val playPauseIntent = Intent(context, MusicyaWidget::class.java).apply {
            action = ACTION_PLAY_PAUSE
        }
        val playPausePendingIntent = PendingIntent.getBroadcast(
            context, 1, playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btn_play_pause, playPausePendingIntent)

        // Next button
        val nextIntent = Intent(context, MusicyaWidget::class.java).apply {
            action = ACTION_NEXT
        }
        val nextPendingIntent = PendingIntent.getBroadcast(
            context, 2, nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btn_next, nextPendingIntent)

        // Previous button
        val prevIntent = Intent(context, MusicyaWidget::class.java).apply {
            action = ACTION_PREVIOUS
        }
        val prevPendingIntent = PendingIntent.getBroadcast(
            context, 3, prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.btn_previous, prevPendingIntent)

        // Update widget UI with current playback state
        val currentSong = playerController?.currentMediaItem?.value
        if (currentSong != null) {
            views.setTextViewText(R.id.tv_title, currentSong.title)
            views.setTextViewText(R.id.tv_artist, currentSong.artist)
            
            // Update album art if available
            try {
                val albumArt = AlbumArtHelper.getAlbumArt(context, currentSong.albumId, 200)
                if (albumArt != null) {
                    views.setImageViewBitmap(R.id.iv_album_art, albumArt)
                } else {
                    views.setImageViewResource(R.id.iv_album_art, android.R.drawable.ic_media_play)
                }
            } catch (e: Exception) {
                views.setImageViewResource(R.id.iv_album_art, android.R.drawable.ic_media_play)
            }

            // Update play/pause icon based on state
            val isPlaying = playerController?.isPlaying?.value ?: false
            views.setImageViewResource(
                R.id.btn_play_pause,
                if (isPlaying) android.R.drawable.ic_media_pause 
                else android.R.drawable.ic_media_play
            )
        } else {
            views.setTextViewText(R.id.tv_title, "No music playing")
            views.setTextViewText(R.id.tv_artist, "Tap to open Musicya")
            views.setImageViewResource(R.id.iv_album_art, android.R.drawable.ic_media_play)
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}