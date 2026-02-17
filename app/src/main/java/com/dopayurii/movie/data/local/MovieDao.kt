package com.dopayurii.movie.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dopayurii.movie.data.model.Movie
import kotlinx.coroutines.flow.Flow

/**
 * Room Data Access Object (DAO) for movie database operations.
 * Provides methods for CRUD operations and queries on the movieDetails table.
 */
@Dao
interface MovieDao {
    /**
     * Inserts or replaces a movie in the database.
     * Uses REPLACE strategy to update existing movies with the same imdbId.
     *
     * @param movie Movie entity to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)


    /**
     * Retrieves a movie by its IMDB ID as a one-time query.
     *
     * @param id IMDB ID of the movie
     * @return Movie entity or null if not found
     */
    @Query("SELECT * FROM movieDetails WHERE imdbId = :id")
    suspend fun getMovieOnce(id: String): Movie?

    /**
     * Deletes a movie from the database.
     *
     * @param movie Movie entity to delete
     */
    @Delete
    suspend fun deleteMovie(movie: Movie)
}
