package com.fourshil.musicya.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log

/**
 * Permission audit utility for logging and tracking permission usage.
 */
object PermissionAudit {

    private const val TAG = "PermissionAudit"

    /**
     * Audit all requested permissions and their status.
     */
    fun auditPermissions(context: Context): PermissionReport {
        val permissions = getDeclaredPermissions(context)
        val permissionStatus = mutableMapOf<String, PermissionStatus>()

        permissions.forEach { permission ->
            val status = when {
                context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED -> {
                    PermissionStatus.GRANTED
                }
                context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED -> {
                    PermissionStatus.GRANTED
                }
                else -> {
                    PermissionStatus.DENIED
                }
            }
            permissionStatus[permission] = status
        }

        Log.d(TAG, "Permission audit complete: ${permissionStatus.count { it.value == PermissionStatus.GRANTED }}/${permissions.size} granted")

        return PermissionReport(
            totalPermissions = permissions.size,
            grantedPermissions = permissionStatus.count { it.value == PermissionStatus.GRANTED },
            permissions = permissionStatus
        )
    }

    /**
     * Get all declared permissions in the app manifest.
     */
    private fun getDeclaredPermissions(context: Context): List<String> {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_PERMISSIONS
            )
            packageInfo.requestedPermissions?.toList() ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get declared permissions", e)
            emptyList()
        }
    }

    /**
     * Check if a specific permission is granted.
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get permissions needed for audio playback.
     */
    fun getAudioPermissions(): List<String> {
        return buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    /**
     * Get all runtime permissions the app uses.
     */
    fun getRuntimePermissions(): List<String> {
        return buildList {
            // Audio
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            
            // Notifications (Android 13+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
            
            // Foreground service (already declared in manifest, but listed here for audit)
        }
    }

    /**
     * Log permission audit results.
     */
    fun logAuditResults(context: Context) {
        val report = auditPermissions(context)
        Log.i(TAG, "=== Permission Audit Report ===")
        Log.i(TAG, "Total: ${report.totalPermissions}")
        Log.i(TAG, "Granted: ${report.grantedPermissions}")
        report.permissions.forEach { (permission, status) ->
            Log.d(TAG, "  $permission: $status")
        }
    }

    data class PermissionReport(
        val totalPermissions: Int,
        val grantedPermissions: Int,
        val permissions: Map<String, PermissionStatus>
    )

    enum class PermissionStatus {
        GRANTED,
        DENIED,
        NEVER_ASK_AGAIN
    }
}

/**
 * Manifest metadata helper for reading app metadata.
 */
object ManifestMetadata {

    private const val TAG = "ManifestMetadata"

    /**
     * Read version information from package info.
     */
    fun getVersionInfo(context: Context): AppVersionInfo {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            AppVersionInfo(
                versionName = packageInfo.versionName ?: "Unknown",
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode.toInt()
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode
                },
                packageName = packageInfo.packageName
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get version info", e)
            AppVersionInfo("Unknown", 0, context.packageName)
        }
    }

    /**
     * Read a custom metadata value from the manifest.
     */
    fun getMetadata(context: Context, key: String): String? {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            appInfo.metaData?.getString(key)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get metadata for key: $key", e)
            null
        }
    }

    data class AppVersionInfo(
        val versionName: String,
        val versionCode: Int,
        val packageName: String
    )
}