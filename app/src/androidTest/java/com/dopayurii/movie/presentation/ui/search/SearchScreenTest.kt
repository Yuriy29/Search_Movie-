package com.dopayurii.movie.presentation.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.data.model.MovieType
import com.dopayurii.movie.domain.model.SearchUiState
import com.dopayurii.movie.presentation.ui.theme.MovieExplorerTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for the Search screen.
 */
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchScreen_displaysInitialState() {
        // Given
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = {},
                    onNavigateToDetails = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Search for movies, series, and episodes")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysSearchBar() {
        // Given
        val uiState = SearchUiState()

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = {},
                    onNavigateToDetails = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithContentDescription("Search")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysLoadingState() {
        // Given
        val uiState = SearchUiState(
            isLoading = true,
            searchResults = emptyList()
        )

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = {},
                    onNavigateToDetails = {}
                )
            }
        }

        // Then - Loading state should not display initial state or results
        composeTestRule.onNodeWithText("Search for movies, series, and episodes")
            .assertDoesNotExist()
        composeTestRule.onNodeWithText("No results found")
            .assertDoesNotExist()
        // The loading indicator (BallPulseProgressIndicator) is a third-party component
        // without content description, so we verify by absence of other content
    }

    @Test
    fun searchScreen_displaysSearchResults() {
        // Given
        val movies = listOf(
            MovieSummary(
                title = "The Batman",
                year = 2022,
                imdbId = "tt1877830",
                type = MovieType.MOVIE,
                poster = "https://example.com/poster.jpg"
            )
        )
        val uiState = SearchUiState(
            query = "Batman",
            searchResults = movies,
            totalResults = 1
        )

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = {},
                    onNavigateToDetails = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNode(hasText("The Batman", substring = true), useUnmergedTree = true)
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNode(hasText("2022", substring = true), useUnmergedTree = true)
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysNoResultsState() {
        // Given
        val uiState = SearchUiState(
            query = "xyznonexistent",
            searchResults = emptyList()
        )

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = {},
                    onNavigateToDetails = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("No results found")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Try different keywords or check your spelling")
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_displaysErrorState() {
        // Given
        val errorMessage = "Network connection failed"
        val uiState = SearchUiState(
            errorMessage = errorMessage,
            searchResults = emptyList()
        )

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = {},
                    onNavigateToDetails = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun searchScreen_clickOnMovieTriggersNavigation() {
        // Given
        val movies = listOf(
            MovieSummary(
                title = "The Batman",
                year = 2022,
                imdbId = "tt1877830",
                type = MovieType.MOVIE,
                poster = "https://example.com/poster.jpg"
            )
        )
        val uiState = SearchUiState(
            query = "Batman",
            searchResults = movies,
            totalResults = 1
        )
        var navigatedMovieId: String? = null

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = {},
                    onNavigateToDetails = { movieId ->
                        navigatedMovieId = movieId
                    }
                )
            }
        }

        composeTestRule.onNodeWithText("The Batman", substring = true)
            .performClick()

        // Then
        assertEquals("tt1877830", navigatedMovieId)
    }

    @Test
    fun searchScreen_queryChangeTriggersEvent() {
        // Given
        val uiState = SearchUiState()
        var lastEvent: SearchUiEvent? = null

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = { event -> lastEvent = event },
                    onNavigateToDetails = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Search")
            .performTextInput("Batman")

        // Then
        assertTrue(lastEvent is SearchUiEvent.OnQueryChange)
        assertEquals("Batman", (lastEvent as SearchUiEvent.OnQueryChange).query)
    }

    @Test
    fun searchScreen_clearClickTriggersEvent() {
        // Given
        val uiState = SearchUiState(query = "Batman")
        var clearEventTriggered = false

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = { event ->
                        if (event is SearchUiEvent.OnClearSearch) {
                            clearEventTriggered = true
                        }
                    },
                    onNavigateToDetails = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear search")
            .performClick()

        // Then
        assertTrue(clearEventTriggered)
    }

    @Test
    fun searchScreen_displaysLoadMoreButton() {
        // Given
        val movies = listOf(
            MovieSummary(
                title = "The Batman",
                year = 2022,
                imdbId = "tt1877830",
                type = MovieType.MOVIE,
                poster = "https://example.com/poster.jpg"
            )
        )
        val uiState = SearchUiState(
            query = "Batman",
            searchResults = movies,
            totalResults = 10,
            hasMoreResults = true
        )

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    onEvent = {},
                    onNavigateToDetails = {}
                )
            }
        }
        composeTestRule.waitForIdle()

        // Then
        composeTestRule.onNode(hasText("Load More", substring = true), useUnmergedTree = true)
            .performScrollTo()
            .assertIsDisplayed()
    }
}
