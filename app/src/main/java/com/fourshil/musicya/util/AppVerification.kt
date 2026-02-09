package com.fourshil.musicya.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * App verification utilities for quality assurance.
 */
object AppVerification {

    private const val TAG = "AppVerification"

    /**
     * Run all verification checks and return a report.
     */
    fun runAllChecks(context: Context): VerificationReport {
        val checks = mutableListOf<CheckResult>()
        
        checks.add(checkPackageName(context))
        checks.add(checkPermissions(context))
        checks.add(checkVersion(context))
        checks.add(checkDeviceInfo(context))
        checks.add(checkStorage(context))
        checks.add(checkMemory(context))
        
        val passed = checks.count { it.passed }
        val total = checks.size
        
        return VerificationReport(
            passedChecks = passed,
            totalChecks = total,
            allPassed = passed == total,
            checks = checks
        )
    }

    private fun checkPackageName(context: Context): CheckResult {
        val expected = "com.fourshil.musicya"
        val actual = context.packageName
        val passed = expected == actual
        return CheckResult(
            name = "Package Name",
            passed = passed,
            message = if (passed) "Package name is correct" else "Expected $expected, got $actual"
        )
    }

    private fun checkPermissions(context: Context): CheckResult {
        val required = listOf(
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.WAKE_LOCK
        )
        
        val missing = required.filter {
            context.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        
        return CheckResult(
            name = "Required Permissions",
            passed = missing.isEmpty(),
            message = if (missing.isEmpty()) "All required permissions declared" else "Missing: ${missing.joinToString()}"
        )
    }

    private fun checkVersion(context: Context): CheckResult {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = info.versionName ?: "Unknown"
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                info.versionCode
            }
            
            CheckResult(
                name = "Version",
                passed = true,
                message = "Version $versionName ($versionCode)"
            )
        } catch (e: Exception) {
            CheckResult(
                name = "Version",
                passed = false,
                message = "Failed to get version: ${e.message}"
            )
        }
    }

    private fun checkDeviceInfo(context: Context): CheckResult {
        return CheckResult(
            name = "Device",
            passed = true,
            message = "${Build.MANUFACTURER} ${Build.MODEL} | Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        )
    }

    private fun checkStorage(context: Context): CheckResult {
        val freeSpace = context.filesDir.freeSpace
        val freeMB = freeSpace / (1024 * 1024)
        val passed = freeMB > 100 // At least 100MB free
        
        return CheckResult(
            name = "Storage",
            passed = passed,
            message = "Free space: ${freeMB}MB" + if (!passed) " (low)" else ""
        )
    }

    private fun checkMemory(context: Context): CheckResult {
        val runtime = Runtime.getRuntime()
        val usedMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
        val maxMB = runtime.maxMemory() / (1024 * 1024)
        val availableMB = maxMB - usedMB
        
        return CheckResult(
            name = "Memory",
            passed = availableMB > 50,
            message = "Available: ${availableMB}MB / ${maxMB}MB"
        )
    }

    data class VerificationReport(
        val passedChecks: Int,
        val totalChecks: Int,
        val allPassed: Boolean,
        val checks: List<CheckResult>
    )

    data class CheckResult(
        val name: String,
        val passed: Boolean,
        val message: String
    )
}

/**
 * Debug utilities for development and testing.
 */
object DebugUtils {

    private const val TAG = "DebugUtils"

    /**
     * Print all app state for debugging.
     */
    fun dumpState(context: Context) {
        Log.d(TAG, "=== App State Dump ===")
        Log.d(TAG, "Package: ${context.packageName}")
        Log.d(TAG, "Version: ${getVersionString(context)}")
        Log.d(TAG, "Device: ${Build.MANUFACTURER} ${Build.MODEL}")
        Log.d(TAG, "Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        Log.d(TAG, "Free Storage: ${context.filesDir.freeSpace / (1024 * 1024)}MB")
        
        val runtime = Runtime.getRuntime()
        Log.d(TAG, "Memory: ${(runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)}MB used")
        Log.d(TAG, "=== End Dump ===")
    }

    /**
     * Get a formatted version string.
     */
    fun getVersionString(context: Context): String {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            "${info.versionName} (${info.longVersionCode})"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    /**
     * Check if running in debug mode.
     */
    fun isDebugBuild(): Boolean {
        return BuildConfig.DEBUG
    }

    /**
     * Get build type string.
     */
    fun getBuildType(): String {
        return BuildConfig.BUILD_TYPE
    }
}