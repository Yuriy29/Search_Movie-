package com.dopayurii.movie.domain.usecase

import androidx.paging.PagingData
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching movies by query with pagination.
 * Returns a Flow of PagingData for use with Paging 3 library.
 *
 * @param query Search string (minimum 2 characters)
 * @return Flow of PagingData containing MovieSummary items
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(query: String): Flow<PagingData<MovieSummary>> {
        return repository.searchMovies(query)
    }
}
