package com.dopayurii.movie.presentation.ui.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.MovieType
import com.dopayurii.movie.domain.model.DetailsUiState
import com.dopayurii.movie.presentation.ui.theme.MovieExplorerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * UI tests for the Details screen.
 */
class DetailsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun detailsScreen_displaysLoadingState() {
        // Given
        val uiState = DetailsUiState(isLoading = true)

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                DetailsScreen(
                    uiState = uiState,
                    onNavigateBack = {}
                )
            }
        }

        // Then - Loading state should not display movie content or error
        composeTestRule.onNodeWithText("The Shawshank Redemption")
            .assertDoesNotExist()
        // The loading indicator (BallPulseProgressIndicator) is a third-party component
        // without content description, so we verify by absence of other content
    }


    @Test
    fun detailsScreen_displaysErrorState() {
        // Given
        val errorMessage = "Failed to load movie details"
        val uiState = DetailsUiState(
            errorMessage = errorMessage,
            movie = null
        )

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                DetailsScreen(
                    uiState = uiState,
                    onNavigateBack = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText(errorMessage)
            .assertIsDisplayed()
    }

    @Test
    fun detailsScreen_backButtonTriggersNavigation() {
        // Given
        val movie = createTestMovie()
        val uiState = DetailsUiState(movie = movie)
        var backPressed = false

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                DetailsScreen(
                    uiState = uiState,
                    onNavigateBack = { backPressed = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate back")
            .performClick()

        // Then
        assertTrue(backPressed)
    }

    @Test
    fun detailsScreen_displaysAllDetailSections() {
        // Given
        val movie = createTestMovie()
        val uiState = DetailsUiState(movie = movie)

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                DetailsScreen(
                    uiState = uiState,
                    onNavigateBack = {}
                )
            }
        }

        // Then - Verify all detail section labels are displayed
        composeTestRule.onNodeWithText("Genres")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Director")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Writer")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Actors")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Languages")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Countries")
            .assertIsDisplayed()
    }

    @Test
    fun detailsScreen_displaysMovieWithYearRange() {
        // Given
        val movie = createTestMovie().copy(
            yearFrom = 2011,
            yearTo = 2019
        )
        val uiState = DetailsUiState(movie = movie)

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                DetailsScreen(
                    uiState = uiState,
                    onNavigateBack = {}
                )
            }
        }

        // Then - Should show year range
        composeTestRule.onNodeWithText("The Shawshank Redemption (2011 - 2019)")
            .assertIsDisplayed()
    }

    @Test
    fun detailsScreen_handlesMissingRating() {
        // Given
        val movie = createTestMovie().copy(imdbRating = null)
        val uiState = DetailsUiState(movie = movie)

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                DetailsScreen(
                    uiState = uiState,
                    onNavigateBack = {}
                )
            }
        }

        // Then - Rating should not be displayed
        composeTestRule.onNodeWithText("IMDb:").assertDoesNotExist()
    }

    @Test
    fun detailsScreen_displaysPoster() {
        // Given
        val movie = createTestMovie()
        val uiState = DetailsUiState(movie = movie)

        // When
        composeTestRule.setContent {
            MovieExplorerTheme {
                DetailsScreen(
                    uiState = uiState,
                    onNavigateBack = {}
                )
            }
        }

        // Then - Poster image with content description should exist
        composeTestRule.onNodeWithContentDescription("The Shawshank Redemption poster")
            .assertExists()
    }

    private fun createTestMovie(): Movie {
        return Movie(
            imdbId = "tt0111161",
            title = "The Shawshank Redemption",
            type = MovieType.MOVIE,
            yearFrom = 1994,
            yearTo = null,
            poster = "https://example.com/poster.jpg",
            genre = listOf("Drama"),
            directors = listOf("Frank Darabont"),
            writers = listOf("Frank Darabont", "Stephen King"),
            actors = listOf("Tim Robbins", "Morgan Freeman"),
            plot = "Two imprisoned men bond over years...",
            language = listOf("English"),
            country = listOf("USA"),
            imdbRating = 9.3,
            seenAtInMillis = System.currentTimeMillis(),
            isFavourite = false
        )
    }
}
