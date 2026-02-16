package com.dopayurii.movie.domain.usecase

import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.MovieType
import com.dopayurii.movie.data.model.Result
import com.dopayurii.movie.domain.repository.MovieRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [FetchMovieDetailsUseCase].
 */
class FetchMovieDetailsUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: FetchMovieDetailsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = FetchMovieDetailsUseCase(repository)
    }

    @Test
    fun `invoke calls repository with correct id`() = runTest {
        // Given
        val movieId = "tt0111161"
        val movie = createTestMovie(movieId)
        coEvery { repository.fetchMovieDetails(movieId) } returns Result.Success(movie)

        // When
        useCase(movieId)

        // Then
        coVerify { repository.fetchMovieDetails(movieId) }
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Given
        val movieId = "tt0111161"
        val movie = createTestMovie(movieId)
        coEvery { repository.fetchMovieDetails(movieId) } returns Result.Success(movie)

        // When
        val result = useCase(movieId)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(movie, (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val movieId = "tt0111161"
        val errorMessage = "Network error"
        coEvery { repository.fetchMovieDetails(movieId) } returns Result.Error(errorMessage)

        // When
        val result = useCase(movieId)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
    }

    private fun createTestMovie(id: String): Movie {
        return Movie(
            imdbId = id,
            title = "The Shawshank Redemption",
            type = MovieType.MOVIE,
            yearFrom = 1994,
            yearTo = null,
            poster = "https://example.com/poster.jpg",
            genre = listOf("Drama"),
            directors = listOf("Frank Darabont"),
            writers = listOf("Frank Darabont"),
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
