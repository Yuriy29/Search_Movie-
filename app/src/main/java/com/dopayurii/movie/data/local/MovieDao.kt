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
     * Retrieves a movie by its IMDB ID as a Flow for reactive updates.
     *
     * @param id IMDB ID of the movie
     * @return Flow emitting the Movie or null if not found
     */
    @Query("SELECT * FROM movieDetails WHERE imdbId = :id")
    fun getMovie(id: String): Flow<Movie?>

    /**
     * Retrieves a movie by its IMDB ID as a one-time query.
     *
     * @param id IMDB ID of the movie
     * @return Movie entity or null if not found
     */
    @Query("SELECT * FROM movieDetails WHERE imdbId = :id")
    suspend fun getMovieOnce(id: String): Movie?

    /**
     * Deletes a movie from the database by its IMDB ID.
     *
     * @param id IMDB ID of the movie to delete
     */
    @Query("DELETE FROM movieDetails WHERE imdbId = :id")
    suspend fun deleteMovie(id: String)

    /**
     * Deletes a movie from the database.
     *
     * @param movie Movie entity to delete
     */
    @Delete
    suspend fun deleteMovie(movie: Movie)

    /**
     * Retrieves all movies ordered by last seen timestamp (most recent first).
     *
     * @return Flow emitting list of all movies ordered by seenAtInMillis DESC
     */
    @Query("SELECT * FROM movieDetails ORDER BY seenAtInMillis DESC")
    fun getAllMovies(): Flow<List<Movie>>

    /**
     * Retrieves all movies ordered by IMDB rating (highest first).
     *
     * @return Flow emitting list of all movies ordered by imdbRating DESC
     */
    @Query("SELECT * FROM movieDetails ORDER BY imdbRating DESC")
    fun getAllMoviesByRating(): Flow<List<Movie>>

    /**
     * Retrieves all movies marked as favourite.
     *
     * @return Flow emitting list of favourite movies
     */
    @Query("SELECT * FROM movieDetails WHERE isFavourite = 1")
    fun getFavouriteMovies(): Flow<List<Movie>>

}
