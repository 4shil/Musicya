package com.fourshil.musicya.player

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.util.Log

/**
 * Manages audio focus for the music player.
 * Handles interruptions from other apps (calls, notifications) and ducking.
 */
class AudioFocusManager(
    private val context: Context,
    private val onAudioFocusGained: () -> Unit,
    private val onAudioFocusLost: (transient: Boolean) -> Unit,
    private val onAudioFocusDucking: () -> Unit
) : AudioManager.OnAudioFocusChangeListener {

    companion object {
        private const val TAG = "AudioFocusManager"
    }

    private val audioManager: AudioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private var focusRequest: AudioFocusRequest? = null
    private var hasAudioFocus = false
    private var wasPlayingBeforeDuck = false

    /**
     * Request audio focus for music playback.
     * Uses AUDIOFOCUS_GAIN for music to ensure uninterrupted playback.
     */
    fun requestAudioFocus(): Boolean {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(this)
            .build()

        val result = audioManager.requestAudioFocus(focusRequest!!)
        hasAudioFocus = result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        
        Log.d(TAG, "Audio focus request result: ${if (hasAudioFocus) "GRANTED" else "DENIED"}")
        return hasAudioFocus
    }

    /**
     * Abandon audio focus when playback stops.
     */
    fun abandonAudioFocus() {
        focusRequest?.let { request ->
            val result = audioManager.abandonAudioFocusRequest(request)
            hasAudioFocus = false
            Log.d(TAG, "Audio focus abandoned: ${result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED}")
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // We gained audio focus - resume playback if we were playing
                Log.d(TAG, "Audio focus gained")
                hasAudioFocus = true
                onAudioFocusGained()
            }
            
            AudioManager.AUDIOFOCUS_LOSS -> {
                // We lost audio focus permanently - stop playback
                Log.d(TAG, "Audio focus lost permanently")
                hasAudioFocus = false
                onAudioFocusLost(transient = false)
            }
            
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // We lost audio focus temporarily - pause playback
                Log.d(TAG, "Audio focus lost transiently")
                onAudioFocusLost(transient = true)
            }
            
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // We can duck (lower volume) and continue playing
                Log.d(TAG, "Audio focus can duck")
                wasPlayingBeforeDuck = hasAudioFocus
                onAudioFocusDucking()
            }
        }
    }

    /**
     * Check if we currently have audio focus.
     */
    fun hasFocus(): Boolean = hasAudioFocus

    /**
     * Check if headphones/Bluetooth are connected.
     */
    fun isAudioOutputAvailable(): Boolean {
        val state = audioManager.mode
        return when (audioManager.isMusicActive) {
            true -> true
            else -> {
                val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
                devices.any { device ->
                    device.type == AudioManager.DEVICE_OUT_WIRED_HEADPHONES ||
                    device.type == AudioManager.DEVICE_OUT_BLUETOOTH_A2DP ||
                    device.type == AudioManager.DEVICE_OUT_BLUETOOTH_SCO ||
                    device.type == AudioManager.DEVICE_OUT_USB
                }
            }
        }
    }
}