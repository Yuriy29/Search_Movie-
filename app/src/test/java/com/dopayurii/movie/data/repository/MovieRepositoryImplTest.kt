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
    fun `searchMovies returns Flow of PagingData`() = runTest {
        // Given
        val query = "Batman"
        val responseSearch = createTestResponseSearch()

        coEvery {
            movieApiService.searchMovies(
                search = any(),
                year = any(),
                page = any(),
                type = any(),
                apiKey = any()
            )
        } returns responseSearch

        // When
        val flow = repository.searchMovies(query)

        // Then - Flow should be created successfully
        assertTrue(flow != null)
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
