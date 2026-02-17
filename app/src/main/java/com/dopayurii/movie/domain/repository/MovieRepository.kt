package com.dopayurii.movie.domain.repository

import androidx.paging.PagingData
import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.data.model.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for movie-related data operations.
 */
interface MovieRepository {
    /**
     * Search for movies by query string with pagination.
     * Returns a Flow of PagingData for use with Paging 3 library.
     *
     * @param query Search query (minimum 2 characters)
     * @return Flow of PagingData containing movie summaries
     */
    fun searchMovies(query: String): Flow<PagingData<MovieSummary>>

    /**
     * Fetch detailed movie information by ID.
     * Checks local cache first, then fetches from network if not available.
     * @param id IMDB ID of the movie
     * @return Result containing movie details or error
     */
    suspend fun fetchMovieDetails(id: String): Result<Movie>

}