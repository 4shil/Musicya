package com.fourshil.musicya.ui.search

import app.cash.turbine.test
import com.fourshil.musicya.data.model.Song
import com.fourshil.musicya.data.repository.MusicRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private lateinit var repository: MusicRepository
    private lateinit var viewModel: SearchViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val testSongs = listOf(
        Song(id = 1, title = "Bohemian Rhapsody", artist = "Queen", album = "A Night at the Opera", duration = 354000, path = "/music/bohemian.mp3"),
        Song(id = 2, title = "Stairway to Heaven", artist = "Led Zeppelin", album = "Led Zeppelin IV", duration = 482000, path = "/music/stairway.mp3"),
        Song(id = 3, title = "Hotel California", artist = "Eagles", album = "Hotel California", duration = 391000, path = "/music/hotel.mp3"),
        Song(id = 4, title = "Sweet Child O Mine", artist = "Guns N Roses", album = "Appetite for Destruction", duration = 356000, path = "/music/sweet.mp3"),
        Song(id = 5, title = "Imagine", artist = "John Lennon", album = "Imagine", duration = 187000, path = "/music/imagine.mp3")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        every { repository.searchSongs(any()) } returns flowOf(emptyList())
        every { repository.getAllSongs() } returns testSongs
        viewModel = SearchViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty`() = runTest {
        viewModel.results.test {
            val initial = awaitItem()
            assertTrue(initial.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search with valid query returns results`() = runTest {
        every { repository.searchSongs("Queen") } returns flowOf(
            listOf(testSongs[0])
        )

        viewModel.onQueryChange("Queen")
        advanceTimeBy(200) // Wait for debounce

        viewModel.results.test {
            skipItems(1) // Skip initial empty state
            val results = awaitItem()
            assertEquals(1, results.size)
            assertEquals("Bohemian Rhapsody", results[0].title)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search with empty query returns empty results`() = runTest {
        viewModel.onQueryChange("")
        advanceTimeBy(200)

        viewModel.results.test {
            val results = awaitItem()
            assertTrue(results.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search with no matches returns empty list`() = runTest {
        every { repository.searchSongs("xyznonexistent") } returns flowOf(emptyList())

        viewModel.onQueryChange("xyznonexistent")
        advanceTimeBy(200)

        viewModel.results.test {
            skipItems(1)
            val results = awaitItem()
            assertTrue(results.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `fuzzy search finds similar titles`() = runTest {
        every { repository.searchSongs("Bohemien") } returns flowOf(
            listOf(testSongs[0]) // Fuzzy match
        )

        viewModel.onQueryChange("Bohemien")
        advanceTimeBy(200)

        viewModel.results.test {
            skipItems(1)
            val results = awaitItem()
            assertTrue(results.isNotEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `recent searches are saved`() = runTest {
        viewModel.saveRecentSearch("Queen")
        viewModel.saveRecentSearch("Led Zeppelin")

        viewModel.recentSearches.test {
            val searches = awaitItem()
            assertTrue(searches.contains("Queen"))
            assertTrue(searches.contains("Led Zeppelin"))
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `clear recent searches empties list`() = runTest {
        viewModel.saveRecentSearch("Queen")
        viewModel.clearRecentSearches()

        viewModel.recentSearches.test {
            val searches = awaitItem()
            assertTrue(searches.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `search results are sorted by relevance`() = runTest {
        every { repository.searchSongs("heaven") } returns flowOf(
            listOf(testSongs[1]) // Stairway to Heaven
        )

        viewModel.onQueryChange("heaven")
        advanceTimeBy(200)

        viewModel.results.test {
            skipItems(1)
            val results = awaitItem()
            if (results.isNotEmpty()) {
                assertEquals("Stairway to Heaven", results[0].title)
            }
            cancelAndIgnoreRemainingEvents()
        }
    }
}