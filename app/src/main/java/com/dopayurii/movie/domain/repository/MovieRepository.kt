package com.dopayurii.movie.domain.repository

import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.MovieSearchResult
import com.dopayurii.movie.data.model.Result

/**
 * Repository interface for movie-related data operations.
 */
interface MovieRepository {
    /**
     * Search for movies by query string with pagination.
     * @param query Search query (minimum 2 characters)
     * @param page Page number (1-100)
     * @return Result of search with pagination info
     */
    suspend fun searchMovies(query: String, page: Int = 1): Result<MovieSearchResult>

    /**
     * Fetch detailed movie information by ID.
     * Checks local cache first, then fetches from network if not available.
     * @param id IMDB ID of the movie
     * @return Result containing movie details or error
     */
    suspend fun fetchMovieDetails(id: String): Result<Movie>

}