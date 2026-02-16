package com.dopayurii.movie.data.repository

import android.util.Log
import com.dopayurii.movie.data.local.MovieDao
import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.MovieType
import com.dopayurii.movie.data.model.Result
import com.dopayurii.movie.data.remote.MovieApiService
import com.dopayurii.movie.data.remote.ResponseMovie
import com.dopayurii.movie.data.remote.ResponseSearch
import com.dopayurii.movie.data.remote.ResponseSearchItem
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [MovieRepositoryImpl].
 */
class MovieRepositoryImplTest {

    private lateinit var movieDao: MovieDao
    private lateinit var movieApiService: MovieApiService
    private lateinit var repository: MovieRepositoryImpl

    @Before
    fun setUp() {
        movieDao = mockk(relaxed = true)
        movieApiService = mockk()
        repository = MovieRepositoryImpl(movieDao, movieApiService)
        // Mock Android Log class to avoid "Method not mocked" errors in unit tests
        mockkStatic(Log::class)
        every { Log.e(any(), any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    // ==================== searchMovies Tests ====================

    @Test
    fun `searchMovies returns empty result when query too short`() = runTest {
        // Given
        val query = "ab"  // Less than 3 characters
        val page = 1

        // When
        val result = repository.searchMovies(query, page)

        // Then
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertTrue(successResult.data.movies.isEmpty())
        assertEquals(0, successResult.data.totalResults)
    }

    @Test
    fun `searchMovies returns success with movies when API succeeds`() = runTest {
        // Given
        val query = "Batman"
        val page = 1
        val responseSearch = createTestResponseSearch()

        // Capture the actual search parameter to verify it's passed correctly
        val searchSlot = slot<String>()
        coEvery {
            movieApiService.searchMovies(
                search = capture(searchSlot),
                year = any(),
                page = any(),
                type = any(),
                apiKey = any()
            )
        } returns responseSearch

        // When
        val result = repository.searchMovies(query, page)

        // Then
        assertEquals("Batman", searchSlot.captured)
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(1, successResult.data.movies.size)
        assertEquals("The Batman", successResult.data.movies[0].title)
        assertEquals(10, successResult.data.totalResults)
    }

    @Test
    fun `searchMovies returns error when API response is False`() = runTest {
        // Given
        val query = "Batman"
        val page = 1
        val errorResponse = ResponseSearch(
            search = emptyList(),
            totalResults = "0",
            response = "False",
            error = "Movie not found!"
        )
        coEvery {
            movieApiService.searchMovies(
                search = any(),
                year = any(),
                page = any(),
                type = any(),
                apiKey = any()
            )
        } returns errorResponse

        // When
        val result = repository.searchMovies(query, page)

        // Then
        assertTrue(result is Result.Error)
        assertEquals("Movie not found!", (result as Result.Error).message)
    }

    @Test
    fun `searchMovies returns error when API throws exception`() = runTest {
        // Given
        val query = "Batman"
        val page = 1
        coEvery {
            movieApiService.searchMovies(
                search = any(),
                year = any(),
                page = any(),
                type = any(),
                apiKey = any()
            )
        } throws RuntimeException("Network error")

        // When
        val result = repository.searchMovies(query, page)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Failed to search movies"))
    }

    // ==================== fetchMovieDetails Tests ====================

    @Test
    fun `fetchMovieDetails returns cached movie when available`() = runTest {
        // Given
        val movieId = "tt0111161"
        val cachedMovie = createTestMovie(movieId).copy(seenAtInMillis = 1000)
        coEvery { movieDao.getMovieOnce(movieId) } returns cachedMovie

        // When
        val result = repository.fetchMovieDetails(movieId)

        // Then
        assertTrue(result is Result.Success)
        val successResult = result as Result.Success
        assertEquals(movieId, successResult.data.imdbId)
        coVerify { movieDao.insertMovie(any()) }  // Should update timestamp
    }

    @Test
    fun `fetchMovieDetails fetches from API when no cache`() = runTest {
        // Given
        val movieId = "tt0111161"
        val remoteMovie = createTestResponseMovie(movieId)
        coEvery { movieDao.getMovieOnce(movieId) } returns null
        coEvery {
            movieApiService.getById(
                id = movieId,
                year = any(),
                type = any(),
                plotLength = any(),
                apiKey = any()
            )
        } returns remoteMovie

        // When
        val result = repository.fetchMovieDetails(movieId)

        // Then
        assertTrue(result is Result.Success)
        coVerify {
            movieApiService.getById(
                id = movieId,
                year = any(),
                type = any(),
                plotLength = any(),
                apiKey = any()
            )
        }
        coVerify { movieDao.insertMovie(any()) }  // Should save to database
    }

    @Test
    fun `fetchMovieDetails returns error when API fails and no cache`() = runTest {
        // Given
        val movieId = "tt0111161"
        coEvery { movieDao.getMovieOnce(movieId) } returns null
        coEvery {
            movieApiService.getById(
                id = any(),
                year = any(),
                type = any(),
                plotLength = any(),
                apiKey = any()
            )
        } throws RuntimeException("Network error")

        // When
        val result = repository.fetchMovieDetails(movieId)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).message.contains("Failed to load movie details"))
    }

    // ==================== Helper Methods ====================

    private fun createTestResponseSearch(): ResponseSearch {
        return ResponseSearch(
            search = listOf(
                ResponseSearchItem(
                    title = "The Batman",
                    year = "2022",
                    imdbId = "tt1877830",
                    type = "movie",
                    poster = "https://example.com/poster.jpg"
                )
            ),
            totalResults = "10",
            response = "True",
            error = null
        )
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

    private fun createTestResponseMovie(id: String): ResponseMovie {
        return ResponseMovie(
            title = "The Shawshank Redemption",
            year = "1994",
            genre = "Drama",
            directors = "Frank Darabont",
            writer = "Frank Darabont",
            actors = "Tim Robbins, Morgan Freeman",
            plot = "Two imprisoned men bond over years...",
            language = "English",
            country = "USA",
            poster = "https://example.com/poster.jpg",
            imdbRating = "9.3",
            imdbId = id,
            type = "movie"
        )
    }
}
