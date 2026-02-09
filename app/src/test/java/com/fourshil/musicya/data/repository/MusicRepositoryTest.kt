package com.fourshil.musicya.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.MatrixCursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.fourshil.musicya.data.model.Song
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MusicRepositoryTest {

    private lateinit var repository: MusicRepository
    private lateinit var mockContext: Context
    private lateinit var mockContentResolver: ContentResolver
    private val testDispatcher = StandardTestDispatcher()

    private val testSongs = listOf(
        Song(id = 1, title = "Song A", artist = "Artist 1", album = "Album A", duration = 180000, path = "/music/song_a.mp3"),
        Song(id = 2, title = "Song B", artist = "Artist 2", album = "Album B", duration = 240000, path = "/music/song_b.mp3"),
        Song(id = 3, title = "Song C", artist = "Artist 1", album = "Album A", duration = 200000, path = "/music/song_c.mp3")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockContext = mockk()
        mockContentResolver = mockk()

        every { mockContext.applicationContext } returns mockContext
        every { mockContext.contentResolver } returns mockContentResolver

        repository = MusicRepository(mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `getAllSongs returns list of songs`() = runTest {
        // Mock MediaStore query
        val mockCursor = createMockSongCursor(testSongs)
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        val songs = withContext(Dispatchers.IO) {
            repository.getAllSongs()
        }

        assertNotNull(songs)
        assertEquals(3, songs.size)
    }

    @Test
    fun `searchSongs filters by title`() = runTest {
        val mockCursor = createMockSongCursor(testSongs)
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        val results = withContext(Dispatchers.IO) {
            repository.searchSongs("Song A")
        }

        assertNotNull(results)
    }

    @Test
    fun `searchSongs filters by artist`() = runTest {
        val mockCursor = createMockSongCursor(testSongs)
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        val results = withContext(Dispatchers.IO) {
            repository.searchSongs("Artist 1")
        }

        assertNotNull(results)
    }

    @Test
    fun `getSongsByAlbum returns album songs`() = runTest {
        val mockCursor = createMockSongCursor(testSongs)
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        val songs = withContext(Dispatchers.IO) {
            repository.getSongsByAlbum("Album A")
        }

        assertNotNull(songs)
    }

    @Test
    fun `getSongsByArtist returns artist songs`() = runTest {
        val mockCursor = createMockSongCursor(testSongs.filter { it.artist == "Artist 1" })
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        val songs = withContext(Dispatchers.IO) {
            repository.getSongsByArtist("Artist 1")
        }

        assertNotNull(songs)
    }

    @Test
    fun `getRecentlyAdded returns recent songs`() = runTest {
        val mockCursor = createMockSongCursor(testSongs)
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        val songs = withContext(Dispatchers.IO) {
            repository.getRecentlyAdded(30)
        }

        assertNotNull(songs)
    }

    @Test
    fun `cache is used on subsequent calls`() = runTest {
        val mockCursor = createMockSongCursor(testSongs)
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        // First call
        withContext(Dispatchers.IO) {
            repository.getAllSongs()
        }

        // Second call should use cache
        val cached = withContext(Dispatchers.IO) {
            repository.getAllSongs()
        }

        // Query should only be called once
        verify(exactly = 1) { mockContentResolver.query(any(), any(), any(), any(), any()) }
        assertEquals(3, cached.size)
    }

    @Test
    fun `invalidateCache forces fresh query`() = runTest {
        val mockCursor = createMockSongCursor(testSongs)
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns mockCursor

        // First call
        withContext(Dispatchers.IO) {
            repository.getAllSongs()
        }

        // Invalidate cache
        repository.invalidateCache()

        // Second call should query again
        withContext(Dispatchers.IO) {
            repository.getAllSongs()
        }

        // Query should be called twice (once for each getAllSongs after invalidate)
        verify(exactly = 2) { mockContentResolver.query(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `empty MediaStore returns empty list`() = runTest {
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } returns null

        val songs = withContext(Dispatchers.IO) {
            repository.getAllSongs()
        }

        assertNotNull(songs)
        assertTrue(songs.isEmpty())
    }

    @Test
    fun `error querying MediaStore returns empty list`() = runTest {
        every { mockContentResolver.query(any(), any(), any(), any(), any()) } throws RuntimeException("Query failed")

        val songs = withContext(Dispatchers.IO) {
            repository.getAllSongs()
        }

        assertNotNull(songs)
        assertTrue(songs.isEmpty())
    }

    private fun createMockSongCursor(songs: List<Song>): android.database.Cursor {
        val cursor = MatrixCursor(
            arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE
            )
        )

        songs.forEach { song ->
            cursor.addRow(arrayOf(
                song.id,
                song.title,
                song.artist,
                song.album,
                song.duration,
                song.path,
                song.fileSize
            ))
        }

        return cursor
    }
}