package com.fourshil.musicya.util

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import android.os.Build
import android.util.Log

/**
 * Coil image loader configuration for optimized album art loading.
 */
object ImageLoaderConfig {

    private const val TAG = "ImageLoader"

    /**
     * Create optimized ImageLoader for album art.
     */
    fun createAlbumArtLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .crossfade(200)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.15) // Use up to 15% of available memory
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("album_art"))
                    .maxSizePercent(0.05) // Use up to 5% of available storage
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .respectCacheHeaders(false)
            .apply {
                if (Build.DEBUG) {
                    logger(DebugLogger())
                }
            }
            .build()
    }

    /**
     * Create lightweight loader for thumbnails.
     */
    fun createThumbnailLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .crossfade(100)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.10)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build()
    }

    /**
     * Clear all image caches.
     */
    suspend fun clearCache(context: Context) {
        try {
            val loader = createAlbumArtLoader(context)
            loader.memoryCache?.clear()
            loader.diskCache?.clear()
            Log.d(TAG, "Image cache cleared")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear image cache", e)
        }
    }

    /**
     * Get cache size info.
     */
    fun getCacheInfo(context: Context): CacheInfo {
        val loader = createAlbumArtLoader(context)
        return CacheInfo(
            memoryCacheSize = loader.memoryCache?.maxSize() ?: 0,
            diskCacheSize = loader.diskCache?.size ?: 0
        )
    }

    data class CacheInfo(
        val memoryCacheSize: Long,
        val diskCacheSize: Long
    ) {
        fun formatMemorySize(): String = formatSize(memoryCacheSize)
        fun formatDiskSize(): String = formatSize(diskCacheSize)

        private fun formatSize(bytes: Long): String {
            return when {
                bytes < 1024 -> "$bytes B"
                bytes < 1024 * 1024 -> "${bytes / 1024} KB"
                else -> "${bytes / (1024 * 1024)} MB"
            }
        }
    }
}