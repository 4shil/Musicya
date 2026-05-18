package com.fourshil.musicya.ui.queue

import app.cash.turbine.test
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.player.PlayerController
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QueueViewModelTest {

    private lateinit var playerController: PlayerController
    private lateinit var viewModel: QueueViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testSongs = listOf(
        Song(id = 1, title = "Song One", artist = "Artist A", album = "Album 1", duration = 180000, path = "/songs/1.mp3"),
        Song(id = 2, title = "Song Two", artist = "Artist B", album = "Album 2", duration = 240000, path = "/songs/2.mp3"),
        Song(id = 3, title = "Song Three", artist = "Artist C", album = "Album 3", duration = 200000, path = "/songs/3.mp3")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        playerController = mockk(relaxed = true)

        // Mock current queue
        every { playerController.queue } returns MutableStateFlow(testSongs)
        every { playerController.currentIndex } returns MutableStateFlow(0)

        viewModel = QueueViewModel(playerController)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial queue contains songs from controller`() = runTest {
        viewModel.queue.test {
            val queue = awaitItem()
            assertEquals(3, queue.size)
            assertEquals("Song One", queue[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `move item changes queue order`() = runTest {
        // Move first item to third position
        viewModel.moveItem(0, 2)
        advanceUntilIdle()

        viewModel.queue.test {
            val queue = awaitItem()
            // After moving index 0 to index 2, order should be: Song 2, Song 3, Song 1
            assertEquals("Song Two", queue[0].title)
            assertEquals("Song Three", queue[1].title)
            assertEquals("Song One", queue[2].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `remove item removes song from queue`() = runTest {
        viewModel.removeFromQueue(1) // Remove second song
        advanceUntilIdle()

        viewModel.queue.test {
            val queue = awaitItem()
            assertEquals(2, queue.size)
            assertFalse(queue.any { it.title == "Song Two" })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clear queue empties list`() = runTest {
        viewModel.clearQueue()
        advanceUntilIdle()

        viewModel.queue.test {
            val queue = awaitItem()
            assertTrue(queue.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `add to queue appends song`() = runTest {
        val newSong = Song(id = 4, title = "New Song", artist = "New Artist", album = "New Album", duration = 300000, path = "/songs/4.mp3")

        viewModel.addToQueue(newSong)
        advanceUntilIdle()

        viewModel.queue.test {
            val queue = awaitItem()
            assertEquals(4, queue.size)
            assertEquals("New Song", queue.last().title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `play next inserts song at current position plus one`() = runTest {
        val newSong = Song(id = 4, title = "Next Song", artist = "New Artist", album = "New Album", duration = 300000, path = "/songs/4.mp3")

        viewModel.playNext(newSong)
        advanceUntilIdle()

        viewModel.queue.test {
            val queue = awaitItem()
            // Should be inserted at position 1 (after current song at index 0)
            assertEquals(4, queue.size)
            assertEquals("Next Song", queue[1].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `shuffle queue randomizes order`() = runTest {
        viewModel.shuffleQueue()
        advanceUntilIdle()

        viewModel.queue.test {
            val queue = awaitItem()
            assertEquals(3, queue.size)
            // All original songs should still be present
            assertTrue(queue.map { it.id }.containsAll(listOf(1L, 2L, 3L)))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `current index updates from controller`() = runTest {
        every { playerController.currentIndex } returns MutableStateFlow(2)

        viewModel.queue.test {
            val queue = awaitItem()
            assertEquals(2, viewModel.currentIndex.value)
            cancelAndIgnoreRemainingEvents()
        }
    }
}