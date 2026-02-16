package com.dopayurii.movie.domain.usecase

import com.dopayurii.movie.data.model.MovieSearchResult
import com.dopayurii.movie.data.model.MovieSummary
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
 * Unit tests for [SearchMoviesUseCase].
 */
class SearchMoviesUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: SearchMoviesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SearchMoviesUseCase(repository)
    }

    @Test
    fun `invoke calls repository with query and page`() = runTest {
        // Given
        val query = "Batman"
        val page = 1
        val searchResult = createTestSearchResult()
        coEvery { repository.searchMovies(query, page) } returns Result.Success(searchResult)

        // When
        useCase(query, page)

        // Then
        coVerify { repository.searchMovies(query, page) }
    }

    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Given
        val query = "Batman"
        val page = 1
        val searchResult = createTestSearchResult()
        coEvery { repository.searchMovies(query, page) } returns Result.Success(searchResult)

        // When
        val result = useCase(query, page)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(searchResult, (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        // Given
        val query = "Batman"
        val page = 1
        val errorMessage = "Network error"
        coEvery { repository.searchMovies(query, page) } returns Result.Error(errorMessage)

        // When
        val result = useCase(query, page)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(errorMessage, (result as Result.Error).message)
    }

    @Test
    fun `invoke uses default page value`() = runTest {
        // Given
        val query = "Batman"
        val searchResult = createTestSearchResult()
        coEvery { repository.searchMovies(query, 1) } returns Result.Success(searchResult)

        // When
        useCase(query)  // No page parameter provided

        // Then
        coVerify { repository.searchMovies(query, 1) }
    }

    private fun createTestSearchResult(): MovieSearchResult {
        val movies = listOf(
            MovieSummary(
                title = "The Batman",
                year = 2022,
                imdbId = "tt1877830",
                type = MovieType.MOVIE,
                poster = "https://example.com/poster.jpg"
            )
        )
        return MovieSearchResult(
            movies = movies,
            totalResults = 1
        )
    }
}
