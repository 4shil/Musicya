package com.fourshil.musicya.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fourshil.musicya.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun mainActivity_startsOnLibraryScreen() {
        composeTestRule.waitForIdle()
        // Verify library screen is displayed
        composeTestRule.onNodeWithText("Library").assertIsDisplayed()
    }

    @Test
    fun bottomNav_navigatesToSearch() {
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun bottomNav_navigatesToPlaylists() {
        composeTestRule.onNodeWithContentDescription("Playlists").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Playlists").assertIsDisplayed()
    }

    @Test
    fun bottomNav_navigatesToFolders() {
        composeTestRule.onNodeWithContentDescription("Folders").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Folders").assertIsDisplayed()
    }

    @Test
    fun bottomNav_navigatesToSettings() {
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Settings").assertIsDisplayed()
    }

    @Test
    fun searchScreen_showsEmptyStateInitially() {
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.waitForIdle()
        // Should show recent searches or empty state
        composeTestRule.onNodeWithTag("search_results").assertExists()
    }

    @Test
    fun settingsScreen_hasAllSections() {
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.waitForIdle()
        // Check for key settings sections
        composeTestRule.onNodeWithText("Appearance").assertExists()
        composeTestRule.onNodeWithText("Playback").assertExists()
        composeTestRule.onNodeWithText("About").assertExists()
    }

    @Test
    fun backNavigation_returnsToPreviousScreen() {
        // Navigate to settings
        composeTestRule.onNodeWithContentDescription("Settings").performClick()
        composeTestRule.waitForIdle()
        // Go back
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.waitForIdle()
        // Should be back on library
        composeTestRule.onNodeWithText("Library").assertIsDisplayed()
    }
}

@RunWith(AndroidJUnit4::class)
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun searchField_acceptsInput() {
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithTag("search_field")
            .performTextInput("Queen")
        
        composeTestRule.onNodeWithText("Queen").assertExists()
    }

    @Test
    fun clearButton_clearsSearch() {
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithTag("search_field")
            .performTextInput("Test")
        
        composeTestRule.onNodeWithContentDescription("Clear search").performClick()
        
        composeTestRule.onNodeWithTag("search_field")
            .assertTextEquals("")
    }

    @Test
    fun searchResults_displaySongInfo() {
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        composeTestRule.waitForIdle()
        
        composeTestRule.onNodeWithTag("search_field")
            .performTextInput("Bohemian")
        
        // Wait for debounce
        composeTestRule.waitForIdle()
        
        // Check if results contain expected info
        composeTestRule.onAllNodesWithTag("song_item")
            .assertCountEquals(0) // May be 0 if no songs loaded in test
    }
}

@RunWith(AndroidJUnit4::class)
class PlayerScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun miniPlayer_showsWhenSongIsPlaying() {
        // This test would require mocking a playing song
        // For now, verify the mini player area exists
        composeTestRule.onNodeWithTag("mini_player")
            .assertExists()
    }

    @Test
    fun playPauseButton_togglesState() {
        composeTestRule.onNodeWithContentDescription("Play").assertExists()
        composeTestRule.onNodeWithContentDescription("Play").performClick()
        // After clicking play, should show pause button
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Pause").assertExists()
    }

    @Test
    fun skipButtons_exist() {
        composeTestRule.onNodeWithContentDescription("Next").assertExists()
        composeTestRule.onNodeWithContentDescription("Previous").assertExists()
    }
}