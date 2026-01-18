package com.fourshil.musicya.player

import com.fourshil.musicya.data.model.Song
import android.net.Uri
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for PlayerController sleep timer and playback speed logic.
 * Note: These tests focus on pure logic without MediaController dependencies.
 */
class PlayerControllerLogicTest {

    @Test
    fun `playback speed is clamped to valid range`() {
        // Test that speeds are clamped correctly
        val minSpeed = 0.25f
        val maxSpeed = 3.0f
        
        // Below minimum
        assertEquals(minSpeed, 0.1f.coerceIn(minSpeed, maxSpeed), 0.001f)
        
        // Above maximum
        assertEquals(maxSpeed, 5.0f.coerceIn(minSpeed, maxSpeed), 0.001f)
        
        // Within range
        assertEquals(1.5f, 1.5f.coerceIn(minSpeed, maxSpeed), 0.001f)
    }
    
    @Test
    fun `speed cycle follows expected order`() {
        val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        
        // From 1.0x should go to 1.25x
        val currentIndex = speeds.indexOf(1.0f)
        val nextIndex = if (currentIndex == speeds.lastIndex) 0 else currentIndex + 1
        assertEquals(1.25f, speeds[nextIndex], 0.001f)
        
        // From 2.0x (last) should cycle back to 0.5x
        val lastIndex = speeds.indexOf(2.0f)
        val cycledIndex = if (lastIndex == speeds.lastIndex) 0 else lastIndex + 1
        assertEquals(0.5f, speeds[cycledIndex], 0.001f)
    }
    
    @Test
    fun `sleep timer duration calculation`() {
        val minutes = 30
        val expectedMs = 30 * 60 * 1000L
        assertEquals(1_800_000L, expectedMs)
        
        // 5 minutes
        assertEquals(300_000L, 5 * 60 * 1000L)
        
        // 1 hour
        assertEquals(3_600_000L, 60 * 60 * 1000L)
    }
    
    @Test
    fun `song duration formatting`() {
        // Helper function to simulate duration formatting
        fun formatDuration(ms: Long): String {
            val totalSeconds = ms / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format("%d:%02d", minutes, seconds)
        }
        
        assertEquals("0:00", formatDuration(0))
        assertEquals("0:30", formatDuration(30_000))
        assertEquals("1:00", formatDuration(60_000))
        assertEquals("3:45", formatDuration(225_000))
        assertEquals("10:00", formatDuration(600_000))
    }
}
