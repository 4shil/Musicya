package com.fourshil.musicya.util

import android.content.Context
import android.util.Log
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Crash reporting and analytics stub for production monitoring.
 * Replace with Firebase Crashlytics or similar in production builds.
 */
object CrashReporter {

    private const val TAG = "CrashReporter"
    private const val CRASH_LOG_DIR = "crash_logs"
    private var isInitialized = false

    /**
     * Initialize crash reporting.
     */
    fun initialize(context: Context) {
        if (isInitialized) return
        
        // Set default uncaught exception handler
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleCrash(context, thread, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
        
        isInitialized = true
        Log.d(TAG, "Crash reporting initialized")
    }

    /**
     * Handle an uncaught exception.
     */
    private fun handleCrash(context: Context, thread: Thread, throwable: Throwable) {
        try {
            val crashLog = generateCrashLog(thread, throwable)
            saveCrashLog(context, crashLog)
            Log.e(TAG, "Crash saved: ${throwable.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save crash log", e)
        }
    }

    /**
     * Generate a crash log string.
     */
    private fun generateCrashLog(thread: Thread, throwable: Throwable): String {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        
        return buildString {
            appendLine("=== Musicya Crash Report ===")
            appendLine("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())}")
            appendLine("Thread: ${thread.name} (ID: ${thread.id})")
            appendLine("Exception: ${throwable.javaClass.name}")
            appendLine("Message: ${throwable.message}")
            appendLine()
            appendLine("Stack Trace:")
            appendLine(sw.toString())
            appendLine()
            appendLine("Device Info:")
            appendLine("  Manufacturer: ${android.os.Build.MANUFACTURER}")
            appendLine("  Model: ${android.os.Build.MODEL}")
            appendLine("  Android Version: ${android.os.Build.VERSION.RELEASE}")
            appendLine("  API Level: ${android.os.Build.VERSION.SDK_INT}")
            appendLine("  ABI: ${android.os.Build.SUPPORTED_ABIS.firstOrNull() ?: "Unknown"}")
        }
    }

    /**
     * Save crash log to internal storage.
     */
    private fun saveCrashLog(context: Context, crashLog: String) {
        val crashDir = File(context.filesDir, CRASH_LOG_DIR)
        if (!crashDir.exists()) crashDir.mkdirs()
        
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val crashFile = File(crashDir, "crash_$timestamp.txt")
        crashFile.writeText(crashLog)
        
        // Keep only last 10 crash logs
        crashDir.listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?.drop(10)
            ?.forEach { it.delete() }
    }

    /**
     * Get all saved crash logs.
     */
    fun getCrashLogs(context: Context): List<File> {
        val crashDir = File(context.filesDir, CRASH_LOG_DIR)
        return crashDir.listFiles()?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    /**
     * Clear all crash logs.
     */
    fun clearCrashLogs(context: Context) {
        val crashDir = File(context.filesDir, CRASH_LOG_DIR)
        crashDir.listFiles()?.forEach { it.delete() }
    }

    /**
     * Log a non-fatal error.
     */
    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e(tag, message, throwable)
        } else {
            Log.e(tag, message)
        }
    }

    /**
     * Log a breadcrumb for debugging.
     */
    fun logBreadcrumb(message: String) {
        Log.d(TAG, "Breadcrumb: $message")
    }
}

/**
 * Analytics stub for tracking user interactions.
 * Replace with Firebase Analytics or similar in production builds.
 */
object Analytics {

    private const val TAG = "Analytics"
    private var isInitialized = false
    private val eventQueue = mutableListOf<AnalyticsEvent>()

    /**
     * Initialize analytics.
     */
    fun initialize(context: Context) {
        isInitialized = true
        Log.d(TAG, "Analytics initialized")
    }

    /**
     * Track a screen view.
     */
    fun trackScreenView(screenName: String, screenClass: String? = null) {
        logEvent("screen_view", mapOf(
            "screen_name" to screenName,
            "screen_class" to (screenClass ?: screenName)
        ))
    }

    /**
     * Track a user action.
     */
    fun trackAction(action: String, params: Map<String, String> = emptyMap()) {
        logEvent("user_action", params + ("action" to action))
    }

    /**
     * Track a playback event.
     */
    fun trackPlayback(event: String, songId: Long? = null, duration: Long? = null) {
        val params = mutableMapOf<String, String>("event" to event)
        songId?.let { params["song_id"] = it.toString() }
        duration?.let { params["duration"] = it.toString() }
        logEvent("playback", params)
    }

    /**
     * Track an error.
     */
    fun trackError(errorType: String, message: String, fatal: Boolean = false) {
        logEvent("error", mapOf(
            "error_type" to errorType,
            "message" to message,
            "fatal" to fatal.toString()
        ))
    }

    /**
     * Track app performance.
     */
    fun trackPerformance(operation: String, durationMs: Long) {
        logEvent("performance", mapOf(
            "operation" to operation,
            "duration_ms" to durationMs.toString()
        ))
    }

    private fun logEvent(eventName: String, params: Map<String, String>) {
        val event = AnalyticsEvent(
            name = eventName,
            params = params,
            timestamp = System.currentTimeMillis()
        )
        eventQueue.add(event)
        Log.d(TAG, "Event: $eventName $params")
    }

    /**
     * Get queued events for batch upload.
     */
    fun getQueuedEvents(): List<AnalyticsEvent> {
        return eventQueue.toList()
    }

    /**
     * Clear queued events after upload.
     */
    fun clearQueuedEvents() {
        eventQueue.clear()
    }

    data class AnalyticsEvent(
        val name: String,
        val params: Map<String, String>,
        val timestamp: Long
    )
}