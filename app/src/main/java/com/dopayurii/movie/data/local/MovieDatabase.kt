package com.dopayurii.movie.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dopayurii.movie.data.model.Movie

/**
 * Room database for the MovieExplorer application.
 * Stores movie details and provides access via MovieDao.
 */
@Database(entities = [Movie::class], version = 1, exportSchema = false)
@TypeConverters(StringListConverter::class)
abstract class MovieDatabase : RoomDatabase() {
    /**
     * Returns the DAO for accessing movie data.
     * @return MovieDao instance
     */
    abstract fun movieDao() : MovieDao
}
