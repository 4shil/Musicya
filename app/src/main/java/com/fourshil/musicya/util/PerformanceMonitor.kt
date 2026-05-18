package com.fourshil.musicya.util

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Performance monitoring and profiling utilities.
 */
object PerformanceMonitor {

    private const val TAG = "PerfMonitor"
    private val timings = mutableMapOf<String, Long>()
    private val metrics = mutableMapOf<String, MutableList<Long>>()

    /**
     * Start timing an operation.
     */
    fun startTiming(label: String) {
        timings[label] = System.nanoTime()
    }

    /**
     * End timing an operation and log the result.
     */
    fun endTiming(label: String): Long {
        val startTime = timings.remove(label) ?: return -1
        val durationMs = (System.nanoTime() - startTime) / 1_000_000
        Log.d(TAG, "$label: ${durationMs}ms")
        
        metrics.getOrPut(label) { mutableListOf() }.add(durationMs)
        return durationMs
    }

    /**
     * Time a block of code.
     */
    inline fun <T> timed(label: String, block: () -> T): T {
        startTiming(label)
        return try {
            block()
        } finally {
            endTiming(label)
        }
    }

    /**
     * Get average timing for a label.
     */
    fun getAverageTiming(label: String): Double {
        val times = metrics[label] ?: return 0.0
        return times.average()
    }

    /**
     * Get performance report.
     */
    fun getReport(): String {
        return buildString {
            appendLine("=== Performance Report ===")
            metrics.forEach { (label, times) ->
                val avg = times.average()
                val min = times.minOrNull() ?: 0
                val max = times.maxOrNull() ?: 0
                appendLine("$label: avg=${avg.toInt()}ms, min=${min}ms, max=${max}ms, count=${times.size}")
            }
        }
    }

    /**
     * Save performance report to file.
     */
    fun saveReport(context: Context) {
        try {
            val report = getReport()
            val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
            val file = File(context.cacheDir, "perf_report_$timestamp.txt")
            file.writeText(report)
            Log.d(TAG, "Performance report saved to ${file.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save performance report", e)
        }
    }

    /**
     * Clear all metrics.
     */
    fun clear() {
        timings.clear()
        metrics.clear()
    }

    /**
     * Memory usage info.
     */
    fun getMemoryInfo(): MemoryInfo {
        val runtime = Runtime.getRuntime()
        val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMem = runtime.maxMemory() / (1024 * 1024)
        return MemoryInfo(usedMem, maxMem, ((usedMem.toFloat() / maxMem) * 100).toInt())
    }

    data class MemoryInfo(
        val usedMb: Long,
        val maxMb: Long,
        val usagePercent: Int
    ) {
        override fun toString(): String = "Memory: ${usedMb}MB / ${maxMb}MB ($usagePercent%)"
    }
}