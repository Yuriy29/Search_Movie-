package com.dopayurii.movie.data.repository

import android.util.Log
import com.dopayurii.movie.data.local.MovieDao
import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.MovieSearchResult
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.data.model.Result
import com.dopayurii.movie.data.remote.MovieApiService
import com.dopayurii.movie.data.remote.toMovieSummary
import com.dopayurii.movie.domain.repository.MovieRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [MovieRepository] that coordinates data between
 * local database (Room) and remote API (OMDb).
 */
@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val movieDao: MovieDao,
    private val movieApiService: MovieApiService
) : MovieRepository {

    companion object {
        private const val TAG = "MovieRepository"
        private const val MIN_QUERY_LENGTH = 3
    }

    override suspend fun searchMovies(query: String, page: Int): Result<MovieSearchResult> {
        if (query.length < MIN_QUERY_LENGTH) {
            return Result.Success(MovieSearchResult())
        }

        return try {
            val response = movieApiService.searchMovies(
                search = query.trim(),
                page = page
            )

            if (response.response == "False") {
                Result.Error(response.error ?: "Search failed")
            } else {
                val movies = response.search.map { it.toMovieSummary() }
                val totalResults = response.totalResults.toIntOrNull() ?: 0
                Result.Success(MovieSearchResult(movies, totalResults))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Search failed for query: $query, page: $page", e)
            Result.Error("Failed to search movies: ${e.message}", e)
        }
    }

    override suspend fun fetchMovieDetails(id: String): Result<Movie> {
        val cachedMovie = movieDao.getMovieOnce(id)

        return if (cachedMovie != null) {
            val updatedMovie = cachedMovie.copy(seenAtInMillis = System.currentTimeMillis())
            movieDao.insertMovie(updatedMovie)
            Result.Success(updatedMovie)
        } else {
            try {
                val remoteMovie = movieApiService.getById(id).toMovie()
                remoteMovie.seenAtInMillis = System.currentTimeMillis()
                movieDao.insertMovie(remoteMovie)
                Result.Success(remoteMovie)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch movie details for id: $id", e)
                Result.Error("Failed to load movie details: ${e.message}", e)
            }
        }
    }

}
