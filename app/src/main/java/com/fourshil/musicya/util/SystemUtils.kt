package com.fourshil.musicya.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.util.Log
import java.io.File

/**
 * Device and system utilities for diagnostics and compatibility.
 */
object SystemUtils {

    private const val TAG = "SystemUtils"

    /**
     * Get device information for debugging.
     */
    fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            device = Build.DEVICE,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            board = Build.BOARD,
            hardware = Build.HARDWARE,
            product = Build.PRODUCT,
            brand = Build.BRAND,
            display = Build.DISPLAY,
            fingerprint = Build.FINGERPRINT,
            host = Build.HOST,
            id = Build.ID,
            tags = Build.TAGS,
            type = Build.TYPE,
            user = Build.USER,
            time = Build.TIME,
            supportedAbis = Build.SUPPORTED_ABIS.toList(),
            isEmulator = isEmulator()
        )
    }

    /**
     * Check if running on emulator.
     */
    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk" == Build.PRODUCT)
    }

    /**
     * Get storage information.
     */
    fun getStorageInfo(context: Context): StorageInfo {
        val dataDir = context.filesDir
        val stat = StatFs(dataDir.path)
        
        val totalBytes = stat.totalBytes
        val freeBytes = stat.availableBytes
        val usedBytes = totalBytes - freeBytes
        
        return StorageInfo(
            totalBytes = totalBytes,
            freeBytes = freeBytes,
            usedBytes = usedBytes,
            totalMB = totalBytes / (1024 * 1024),
            freeMB = freeBytes / (1024 * 1024),
            usedMB = usedBytes / (1024 * 1024),
            usagePercent = (usedBytes.toFloat() / totalBytes * 100).toInt()
        )
    }

    /**
     * Get memory information.
     */
    fun getMemoryInfo(): MemoryInfo {
        val runtime = Runtime.getRuntime()
        val maxMemory = runtime.maxMemory()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()
        val usedMemory = totalMemory - freeMemory
        
        return MemoryInfo(
            maxBytes = maxMemory,
            totalBytes = totalMemory,
            freeBytes = freeMemory,
            usedBytes = usedMemory,
            maxMB = maxMemory / (1024 * 1024),
            totalMB = totalMemory / (1024 * 1024),
            freeMB = freeMemory / (1024 * 1024),
            usedMB = usedMemory / (1024 * 1024)
        )
    }

    /**
     * Check if external storage is available.
     */
    fun isExternalStorageAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    /**
     * Get external storage path.
     */
    fun getExternalStoragePath(): String? {
        return if (isExternalStorageAvailable()) {
            Environment.getExternalStorageDirectory().absolutePath
        } else null
    }

    /**
     * Open app settings.
     */
    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /**
     * Open notification settings.
     */
    fun openNotificationSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    data class DeviceInfo(
        val manufacturer: String,
        val model: String,
        val device: String,
        val androidVersion: String,
        val apiLevel: Int,
        val board: String,
        val hardware: String,
        val product: String,
        val brand: String,
        val display: String,
        val fingerprint: String,
        val host: String,
        val id: String,
        val tags: String,
        val type: String,
        val user: String,
        val time: Long,
        val supportedAbis: List<String>,
        val isEmulator: Boolean
    )

    data class StorageInfo(
        val totalBytes: Long,
        val freeBytes: Long,
        val usedBytes: Long,
        val totalMB: Long,
        val freeMB: Long,
        val usedMB: Long,
        val usagePercent: Int
    )

    data class MemoryInfo(
        val maxBytes: Long,
        val totalBytes: Long,
        val freeBytes: Long,
        val usedBytes: Long,
        val maxMB: Long,
        val totalMB: Long,
        val freeMB: Long,
        val usedMB: Long
    )
}