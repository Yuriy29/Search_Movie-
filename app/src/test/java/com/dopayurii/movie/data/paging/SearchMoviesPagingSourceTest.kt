package com.dopayurii.movie.data.paging

import android.util.Log
import androidx.paging.PagingSource
import com.dopayurii.movie.data.remote.MovieApiService
import com.dopayurii.movie.data.remote.ResponseSearch
import com.dopayurii.movie.data.remote.ResponseSearchItem
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for [SearchMoviesPagingSource].
 */
class SearchMoviesPagingSourceTest {

    private lateinit var apiService: MovieApiService
    private lateinit var pagingSource: SearchMoviesPagingSource

    @Before
    fun setUp() {
        apiService = mockk()
        mockkStatic(Log::class)
        every { Log.d(any<String>(), any<String>()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    @Test
    fun `load returns page when API succeeds`() = runTest {
        // Given
        val query = "Batman"
        val response = createTestResponseSearch()
        coEvery {
            apiService.searchMovies(
                search = query,
                year = any(),
                page = 1,
                type = any(),
                apiKey = any()
            )
        } returns response

        pagingSource = SearchMoviesPagingSource(apiService, query)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(10, page.data.size)
        assertEquals("Movie 0", page.data[0].title)
        assertNull(page.prevKey)
        assertEquals(2, page.nextKey) // Has more pages since 10 < 50
    }

    @Test
    fun `load returns empty page when query is too short`() = runTest {
        // Given
        val query = "a" // Too short
        pagingSource = SearchMoviesPagingSource(apiService, query)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    @Test
    fun `load returns empty page when API response is False`() = runTest {
        // Given
        val query = "NonExistentMovie12345"
        val errorResponse = ResponseSearch(
            search = emptyList(),
            totalResults = "0",
            response = "False",
            error = "Movie not found!"
        )
        coEvery {
            apiService.searchMovies(
                search = query,
                year = any(),
                page = any(),
                type = any(),
                apiKey = any()
            )
        } returns errorResponse

        pagingSource = SearchMoviesPagingSource(apiService, query)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertTrue(page.data.isEmpty())
        assertNull(page.prevKey)
        assertNull(page.nextKey)
    }

    @Test
    fun `load returns error when API throws exception`() = runTest {
        // Given
        val query = "Batman"
        coEvery {
            apiService.searchMovies(
                search = query,
                year = any(),
                page = any(),
                type = any(),
                apiKey = any()
            )
        } throws RuntimeException("Network error")

        pagingSource = SearchMoviesPagingSource(apiService, query)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Error)
    }

    @Test
    fun `load returns correct prevKey and nextKey for middle page`() = runTest {
        // Given
        val query = "Batman"
        val response = createTestResponseSearch(totalResults = "30")
        coEvery {
            apiService.searchMovies(
                search = query,
                year = any(),
                page = 2,
                type = any(),
                apiKey = any()
            )
        } returns response

        pagingSource = SearchMoviesPagingSource(apiService, query)

        // When - Load page 2
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = 2,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(10, page.data.size)
        assertEquals(1, page.prevKey) // Previous page is 1
        assertEquals(3, page.nextKey) // Next page is 3 (20 loaded, 30 total)
    }

    @Test
    fun `load returns null nextKey when no more pages`() = runTest {
        // Given
        val query = "Batman"
        // 10 results total, loading page 1 means no more pages
        val response = createTestResponseSearch(totalResults = "10")
        coEvery {
            apiService.searchMovies(
                search = query,
                year = any(),
                page = 1,
                type = any(),
                apiKey = any()
            )
        } returns response

        pagingSource = SearchMoviesPagingSource(apiService, query)

        // When
        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = 10,
                placeholdersEnabled = false
            )
        )

        // Then
        assertTrue(result is PagingSource.LoadResult.Page)
        val page = result as PagingSource.LoadResult.Page
        assertEquals(10, page.data.size)
        assertNull(page.prevKey)
        assertNull(page.nextKey) // No more pages (10 loaded, 10 total)
    }

    private fun createTestResponseSearch(totalResults: String = "50"): ResponseSearch {
        val count = totalResults.toIntOrNull() ?: 0
        val itemsPerPage = minOf(count, 10)
        val searchItems = List(itemsPerPage) { index ->
            ResponseSearchItem(
                title = "Movie $index",
                year = "202${index % 10}",
                imdbId = "tt187783$index",
                type = "movie",
                poster = "https://example.com/poster$index.jpg"
            )
        }
        return ResponseSearch(
            search = searchItems,
            totalResults = totalResults,
            response = "True",
            error = null
        )
    }
}
