package com.fourshil.musicya.util

import android.content.Context
import android.content.SharedPreferences
import io.mockk.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class PlaybackStatsTest {

    private lateinit var mockContext: Context
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        mockContext = mockk()
        mockPrefs = mockk()
        mockEditor = mockk(relaxed = true)

        every { mockContext.getSharedPreferences(any(), any()) } returns mockPrefs
        every { mockPrefs.edit() } returns mockEditor
        every { mockEditor.putInt(any(), any()) } returns mockEditor
        every { mockEditor.putLong(any(), any()) } returns mockEditor
        every { mockEditor.apply() } just runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `increment play count updates counter`() {
        every { mockPrefs.getInt(PlaybackStats.KEY_TOTAL_SONGS_PLAYED, 0) } returns 5

        // Reset to test value
        PlaybackStats.init(mockContext)
        PlaybackStats.incrementPlayCount()

        verify { mockEditor.putInt(PlaybackStats.KEY_TOTAL_SONGS_PLAYED, 6) }
    }

    @Test
    fun `add listening time accumulates duration`() {
        every { mockPrefs.getLong(PlaybackStats.KEY_TOTAL_LISTENING_TIME, 0) } returns 3600000L // 1 hour

        PlaybackStats.init(mockContext)
        PlaybackStats.addListeningTime(600000L) // Add 10 minutes

        verify { mockEditor.putLong(PlaybackStats.KEY_TOTAL_LISTENING_TIME, 4200000L) }
    }

    @Test
    fun `record song play updates last played`() {
        PlaybackStats.init(mockContext)
        PlaybackStats.recordSongPlayed(123L)

        verify { mockEditor.putLong(PlaybackStats.KEY_LAST_PLAYED_SONG_ID, 123L) }
        verify { mockEditor.putLong(eq(PlaybackStats.KEY_LAST_PLAYED_TIMESTAMP), any()) }
    }

    @Test
    fun `get play count returns correct value`() {
        every { mockPrefs.getInt(PlaybackStats.KEY_TOTAL_SONGS_PLAYED, 0) } returns 42

        PlaybackStats.init(mockContext)
        val count = PlaybackStats.getPlayCount(123L)

        assertEquals(42, count)
    }

    @Test
    fun `get total listening time returns correct value`() {
        val expectedTime = 7200000L // 2 hours
        every { mockPrefs.getLong(PlaybackStats.KEY_TOTAL_LISTENING_TIME, 0) } returns expectedTime

        PlaybackStats.init(mockContext)
        val time = PlaybackStats.getTotalListeningTime()

        assertEquals(expectedTime, time)
    }

    @Test
    fun `reset stats clears all data`() {
        PlaybackStats.init(mockContext)
        PlaybackStats.resetStats()

        verify { mockEditor.putInt(PlaybackStats.KEY_TOTAL_SONGS_PLAYED, 0) }
        verify { mockEditor.putLong(PlaybackStats.KEY_TOTAL_LISTENING_TIME, 0L) }
        verify { mockEditor.putLong(PlaybackStats.KEY_LAST_PLAYED_SONG_ID, -1L) }
        verify { mockEditor.putLong(PlaybackStats.KEY_LAST_PLAYED_TIMESTAMP, 0L) }
    }
}

class SleepTimerTest {

    @Test
    fun `preset durations are correct`() {
        val presets = listOf(5, 10, 15, 30, 45, 60, 90, 120)
        presets.forEach { minutes ->
            assertTrue(minutes in 1..480) // Between 1 minute and 8 hours
        }
    }

    @Test
    fun `calculate remaining time is accurate`() {
        val startTime = System.currentTimeMillis()
        val duration = 15 * 60 * 1000L // 15 minutes

        // Simulate 5 minutes passing
        val elapsed = 5 * 60 * 1000L
        val remaining = SleepTimer.calculateRemainingTime(startTime, duration, elapsed)

        assertTrue(remaining > 0)
        assertTrue(remaining <= 10 * 60 * 1000L) // Less than 10 minutes
    }

    @Test
    fun `zero duration means end of track`() {
        val duration = 0
        assertEquals(SleepTimer.StopAction.END_OF_TRACK, SleepTimer.StopAction.fromMinutes(duration))
    }
}

class QueueUtilsTest {

    private val testSongs = listOf(
        createTestSong(1, 180000L),
        createTestSong(2, 240000L),
        createTestSong(3, 200000L)
    )

    @Test
    fun `calculate total duration sums all songs`() {
        val total = QueueUtils.calculateTotalDuration(testSongs)
        assertEquals(620000L, total) // 3:00 + 4:00 + 3:20
    }

    @Test
    fun `format duration formats correctly`() {
        assertEquals("3:00", QueueUtils.formatDuration(180000L))
        assertEquals("10:30", QueueUtils.formatDuration(630000L))
        assertEquals("1:30:45", QueueUtils.formatDuration(5445000L)) // Over an hour
    }

    @Test
    fun `empty list returns zero duration`() {
        val total = QueueUtils.calculateTotalDuration(emptyList())
        assertEquals(0L, total)
    }

    @Test
    fun `shuffle queue contains all songs`() {
        val shuffled = QueueUtils.shuffleQueue(testSongs)
        assertEquals(3, shuffled.size)
        assertTrue(shuffled.map { it.id }.containsAll(listOf(1L, 2L, 3L)))
    }

    private fun createTestSong(id: Long, duration: Long) = object : com.fourshil.musicya.data.model.Song {
        override val id: Long = id
        override val title: String = "Test Song $id"
        override val artist: String = "Test Artist"
        override val album: String = "Test Album"
        override val duration: Long = duration
        override val path: String = "/test/$id.mp3"
        override val fileSize: Long = 0L
        override val bitrate: Int = 0
        override val sampleRate: Int = 0
        override val channels: Int = 0
    }
}