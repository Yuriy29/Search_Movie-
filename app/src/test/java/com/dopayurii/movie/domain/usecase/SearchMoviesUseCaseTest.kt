package com.dopayurii.movie.domain.usecase

import androidx.paging.PagingData
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.domain.repository.MovieRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
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
    fun `invoke calls repository searchMovies with query`() = runTest {
        // Given
        val query = "Batman"
        val pagingData = PagingData.empty<MovieSummary>()
        every { repository.searchMovies(query) } returns flowOf(pagingData)

        // When
        useCase(query)

        // Then
        verify { repository.searchMovies(query) }
    }

    @Test
    fun `invoke returns Flow of PagingData`() = runTest {
        // Given
        val query = "Batman"
        val pagingData = PagingData.empty<MovieSummary>()
        every { repository.searchMovies(query) } returns flowOf(pagingData)

        // When
        val result: Flow<PagingData<MovieSummary>> = useCase(query)

        // Then
        assertNotNull(result)
        assertNotNull(result.first())
    }
}
