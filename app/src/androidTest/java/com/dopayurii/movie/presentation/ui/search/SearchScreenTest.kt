package com.dopayurii.movie.presentation.ui.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.presentation.ui.model.SearchUiState
import com.dopayurii.movie.presentation.ui.theme.MovieExplorerTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for the Search screen with Paging 3.
 */
class SearchScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchScreen_displaysInitialState() {
        // Given
        val uiState = SearchUiState()
        val emptyFlow = flowOf(PagingData.empty<MovieSummary>())

        // When
        composeTestRule.setContent {
            val movies = emptyFlow.collectAsLazyPagingItems()
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    movies = movies,
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
        val emptyFlow = flowOf(PagingData.empty<MovieSummary>())

        // When
        composeTestRule.setContent {
            val movies = emptyFlow.collectAsLazyPagingItems()
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    movies = movies,
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
    fun searchScreen_queryChangeTriggersEvent() {
        // Given
        val uiState = SearchUiState()
        val emptyFlow = flowOf(PagingData.empty<MovieSummary>())
        var lastEvent: SearchUiEvent? = null

        // When
        composeTestRule.setContent {
            val movies = emptyFlow.collectAsLazyPagingItems()
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    movies = movies,
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
        val emptyFlow = flowOf(PagingData.empty<MovieSummary>())
        var clearEventTriggered = false

        // When
        composeTestRule.setContent {
            val movies = emptyFlow.collectAsLazyPagingItems()
            MovieExplorerTheme {
                SearchScreen(
                    uiState = uiState,
                    movies = movies,
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
}
