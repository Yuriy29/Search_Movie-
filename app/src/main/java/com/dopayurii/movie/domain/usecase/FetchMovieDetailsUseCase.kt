package com.dopayurii.movie.domain.usecase

import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.Result
import com.dopayurii.movie.domain.repository.MovieRepository
import javax.inject.Inject

/**
 * Use case for fetching detailed movie information.
 * Checks local cache first, then fetches from network if needed.
 */
class FetchMovieDetailsUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(id: String): Result<Movie> {
        return repository.fetchMovieDetails(id)
    }
}
