package com.fourshil.musicya.util

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.CountDownTimer
import android.os.PowerManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Sleep timer utility with multiple stop actions.
 */
object SleepTimer {

    private const val TAG = "SleepTimer"

    enum class StopAction {
        STOP_PLAYBACK,      // Just stop playback
        PAUSE_PLAYBACK,     // Pause (can resume)
        CLOSE_APP,          // Close the app
        DO_NOTHING          // Just dim volume
    }

    private val _remainingTimeMs = MutableStateFlow<Long?>(null)
    val remainingTimeMs: StateFlow<Long?> = _remainingTimeMs.asStateFlow()

    private val _isActive = MutableStateFlow(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    private var timer: CountDownTimer? = null
    private var stopAction: StopAction = StopAction.STOP_PLAYBACK
    private var originalVolume: Int = -1
    private var wakeLock: PowerManager.WakeLock? = null

    // Callback to be set by PlayerController
    var onStopAction: ((StopAction) -> Unit)? = null

    /**
     * Start the sleep timer.
     * @param durationMs Duration in milliseconds
     * @param action What to do when timer expires
     */
    fun start(durationMs: Long, action: StopAction = StopAction.STOP_PLAYBACK) {
        cancel() // Cancel any existing timer

        stopAction = action
        _remainingTimeMs.value = durationMs
        _isActive.value = true

        timer = object : CountDownTimer(durationMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _remainingTimeMs.value = millisUntilFinished
            }

            override fun onFinish() {
                _remainingTimeMs.value = 0
                executeStopAction()
                _isActive.value = false
                _remainingTimeMs.value = null
            }
        }.start()

        Log.d(TAG, "Sleep timer started: ${durationMs / 1000}s, action: $action")
    }

    /**
     * Cancel the sleep timer.
     */
    fun cancel() {
        timer?.cancel()
        timer = null
        releaseWakeLock()
        _isActive.value = false
        _remainingTimeMs.value = null
        Log.d(TAG, "Sleep timer cancelled")
    }

    private fun executeStopAction() {
        Log.d(TAG, "Sleep timer expired, executing: $stopAction")
        onStopAction?.invoke(stopAction)

        when (stopAction) {
            StopAction.STOP_PLAYBACK -> {
                // Handled by onStopAction callback
            }
            StopAction.PAUSE_PLAYBACK -> {
                // Handled by onStopAction callback
            }
            StopAction.CLOSE_APP -> {
                // Would need context to close app
            }
            StopAction.DO_NOTHING -> {
                // Just the timer expired
            }
        }
    }

    private fun acquireWakeLock(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Musicya:SleepTimer"
        ).apply {
            acquire(5 * 60 * 1000L) // 5 minute max
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        wakeLock = null
    }

    /**
     * Format remaining time as MM:SS or HH:MM:SS.
     */
    fun formatRemaining(): String {
        val ms = _remainingTimeMs.value ?: return "--:--"
        val totalSeconds = ms / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%d:%02d", minutes, seconds)
        }
    }
}