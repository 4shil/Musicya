package com.fourshil.musicya

import android.app.Application
import android.util.Log
import com.fourshil.musicya.util.CrashReporter
import com.fourshil.musicya.util.Analytics
import dagger.hilt.android.HiltAndroidApp

/**
 * Musicya Application class.
 * Initializes crash reporting, analytics, and Hilt dependency injection.
 */
@HiltAndroidApp
class MusicyaApp : Application() {

    companion object {
        private const val TAG = "MusicyaApp"
        lateinit var instance: MusicyaApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Initialize crash reporting
        CrashReporter.initialize(this)
        
        // Initialize analytics
        Analytics.initialize(this)
        
        Log.d(TAG, "Musicya application initialized")
    }
}