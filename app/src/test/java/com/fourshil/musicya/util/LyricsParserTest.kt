package com.fourshil.musicya.util

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for LyricsParser.
 * Tests LRC format parsing including timestamps, metadata, and edge cases.
 */
class LyricsParserTest {

    @Test
    fun `parse simple LRC with timestamps`() {
        val lrc = """
            [00:00.00]First line
            [00:05.50]Second line
            [00:10.25]Third line
        """.trimIndent()
        
        val lyrics = LyricsParser.parse(lrc)
        
        assertEquals(3, lyrics.lines.size)
        assertEquals(0L, lyrics.lines[0].timeMs)
        assertEquals("First line", lyrics.lines[0].text)
        assertEquals(5500L, lyrics.lines[1].timeMs)
        assertEquals("Second line", lyrics.lines[1].text)
        assertEquals(10250L, lyrics.lines[2].timeMs)
        assertEquals("Third line", lyrics.lines[2].text)
    }
    
    @Test
    fun `parse LRC with metadata tags`() {
        val lrc = """
            [ti:Song Title]
            [ar:Artist Name]
            [al:Album Name]
            [00:00.00]Lyrics start here
        """.trimIndent()
        
        val lyrics = LyricsParser.parse(lrc)
        
        assertEquals("Song Title", lyrics.title)
        assertEquals("Artist Name", lyrics.artist)
        assertEquals("Album Name", lyrics.album)
        assertEquals(1, lyrics.lines.size)
    }
    
    @Test
    fun `parse LRC with colon timestamp format`() {
        val lrc = """
            [01:30:50]Line at 1:30.50
        """.trimIndent()
        
        val lyrics = LyricsParser.parse(lrc)
        
        assertEquals(1, lyrics.lines.size)
        // 1 minute 30 seconds 500ms = 90500ms
        assertEquals(90500L, lyrics.lines[0].timeMs)
    }
    
    @Test
    fun `parse LRC with milliseconds format`() {
        val lrc = """
            [00:05.123]Line with milliseconds
        """.trimIndent()
        
        val lyrics = LyricsParser.parse(lrc)
        
        assertEquals(1, lyrics.lines.size)
        assertEquals(5123L, lyrics.lines[0].timeMs)
    }
    
    @Test
    fun `parse LRC with multiple timestamps per line`() {
        val lrc = """
            [00:10.00][00:30.00][00:50.00]Repeated chorus line
        """.trimIndent()
        
        val lyrics = LyricsParser.parse(lrc)
        
        assertEquals(3, lyrics.lines.size)
        assertTrue(lyrics.lines.all { it.text == "Repeated chorus line" })
        assertEquals(10000L, lyrics.lines[0].timeMs)
        assertEquals(30000L, lyrics.lines[1].timeMs)
        assertEquals(50000L, lyrics.lines[2].timeMs)
    }
    
    @Test
    fun `lines are sorted by timestamp`() {
        val lrc = """
            [00:30.00]Line 3
            [00:10.00]Line 1
            [00:20.00]Line 2
        """.trimIndent()
        
        val lyrics = LyricsParser.parse(lrc)
        
        assertEquals(3, lyrics.lines.size)
        assertEquals("Line 1", lyrics.lines[0].text)
        assertEquals("Line 2", lyrics.lines[1].text)
        assertEquals("Line 3", lyrics.lines[2].text)
    }
    
    @Test
    fun `empty lines are skipped`() {
        val lrc = """
            [00:00.00]First
            
            [00:10.00]Second
        """.trimIndent()
        
        val lyrics = LyricsParser.parse(lrc)
        
        assertEquals(2, lyrics.lines.size)
    }
    
    @Test
    fun `getCurrentLineIndex returns correct index`() {
        val lyrics = Lyrics(
            lines = listOf(
                LyricLine(0, "Line 1"),
                LyricLine(5000, "Line 2"),
                LyricLine(10000, "Line 3")
            )
        )
        
        assertEquals(0, lyrics.getCurrentLineIndex(0))
        assertEquals(0, lyrics.getCurrentLineIndex(2500))
        assertEquals(1, lyrics.getCurrentLineIndex(5000))
        assertEquals(1, lyrics.getCurrentLineIndex(7500))
        assertEquals(2, lyrics.getCurrentLineIndex(10000))
        assertEquals(2, lyrics.getCurrentLineIndex(15000))
    }
    
    @Test
    fun `empty lyrics returns -1 for getCurrentLineIndex`() {
        val lyrics = Lyrics.EMPTY
        assertEquals(-1, lyrics.getCurrentLineIndex(1000))
    }
}
