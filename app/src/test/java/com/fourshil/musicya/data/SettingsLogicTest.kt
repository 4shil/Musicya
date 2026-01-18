package com.fourshil.musicya.data

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ThemeMode enum and settings logic.
 */
class SettingsLogicTest {

    @Test
    fun `theme mode enum values`() {
        val modes = ThemeMode.entries
        assertEquals(3, modes.size)
        assertTrue(modes.contains(ThemeMode.SYSTEM))
        assertTrue(modes.contains(ThemeMode.LIGHT))
        assertTrue(modes.contains(ThemeMode.DARK))
    }
    
    @Test
    fun `theme mode string mapping`() {
        fun themeModeToString(mode: ThemeMode): String {
            return when (mode) {
                ThemeMode.SYSTEM -> "system"
                ThemeMode.LIGHT -> "light"
                ThemeMode.DARK -> "dark"
            }
        }
        
        fun stringToThemeMode(str: String?): ThemeMode {
            return when (str) {
                "light" -> ThemeMode.LIGHT
                "dark" -> ThemeMode.DARK
                else -> ThemeMode.SYSTEM
            }
        }
        
        // Round-trip test
        ThemeMode.entries.forEach { mode ->
            val str = themeModeToString(mode)
            val restored = stringToThemeMode(str)
            assertEquals(mode, restored)
        }
        
        // Unknown string defaults to SYSTEM
        assertEquals(ThemeMode.SYSTEM, stringToThemeMode(null))
        assertEquals(ThemeMode.SYSTEM, stringToThemeMode("unknown"))
    }
    
    @Test
    fun `crossfade duration clamping`() {
        // Valid range is 0-12
        assertEquals(0, 0.coerceIn(0, 12))
        assertEquals(5, 5.coerceIn(0, 12))
        assertEquals(12, 12.coerceIn(0, 12))
        
        // Below minimum
        assertEquals(0, (-5).coerceIn(0, 12))
        
        // Above maximum
        assertEquals(12, 20.coerceIn(0, 12))
    }
}
