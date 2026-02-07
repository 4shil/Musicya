package com.fourshil.musicya.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.core.content.getSystemService

/**
 * Haptic feedback utilities for tactile responses.
 */
object HapticFeedback {
    
    private const val CLICK_DURATION = 10L
    private const val HEAVY_DURATION = 30L
    
    /**
     * Light click feedback - for button presses and selections.
     */
    fun performClick(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }
    
    /**
     * Heavy click feedback - for important actions like delete or confirm.
     */
    fun performHeavyClick(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
    
    /**
     * Long press feedback - for drag operations.
     */
    fun performLongPress(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
    }
    
    /**
     * Vibrate with custom pattern.
     */
    fun vibrate(context: Context, durationMs: Long = CLICK_DURATION) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService<VibratorManager>()
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService<Vibrator>()
        }
        
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when {
                    durationMs <= CLICK_DURATION -> VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                    durationMs <= HEAVY_DURATION -> VibrationEffect.createOneShot(durationMs, VibrationEffect.EFFECT_HEAVY_CLICK)
                    else -> VibrationEffect.createOneShot(durationMs, VibrationEffect.EFFECT_DOUBLE_CLICK)
                }
                it.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(durationMs)
            }
        }
    }
    
    /**
     * Short vibration for list item interactions.
     */
    fun listItemClick(context: Context) {
        vibrate(context, CLICK_DURATION)
    }
}