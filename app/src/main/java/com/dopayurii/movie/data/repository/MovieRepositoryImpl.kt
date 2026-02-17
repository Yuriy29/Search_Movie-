package com.dopayurii.movie.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.dopayurii.movie.data.local.MovieDao
import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.data.model.Result
import com.dopayurii.movie.data.paging.SearchMoviesPagingSource
import com.dopayurii.movie.data.remote.MovieApiService
import com.dopayurii.movie.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
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
        private const val PAGE_SIZE = 10
    }

    /**
     * Returns a Flow of PagingData for searching movies.
     * Uses Paging 3 library with SearchMoviesPagingSource.
     *
     * @param query Search query string
     * @return Flow of PagingData containing MovieSummary items
     */
    override fun searchMovies(query: String): Flow<PagingData<MovieSummary>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = {
                SearchMoviesPagingSource(movieApiService, query)
            }
        ).flow
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
