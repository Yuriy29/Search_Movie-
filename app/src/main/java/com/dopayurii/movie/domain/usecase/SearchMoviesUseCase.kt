package com.dopayurii.movie.domain.usecase

import com.dopayurii.movie.data.model.MovieSearchResult
import com.dopayurii.movie.data.model.Result
import com.dopayurii.movie.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * Use case for searching movies by query with pagination.
 * @param query Search string (minimum 2 characters)
 * @param page Page number (1-100)
 */
class SearchMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(query: String, page: Int = 1): Result<MovieSearchResult> {
        return repository.searchMovies(query, page)
    }
}
