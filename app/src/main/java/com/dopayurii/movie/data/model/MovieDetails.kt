package com.dopayurii.movie.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Room entity representing detailed movie information.
 * Stores comprehensive movie data from OMDb API including cast, crew, ratings, and plot.
 *
 * @property imdbId Unique IMDB identifier (primary key)
 * @property title Movie title
 * @property type Type of content (movie, series, episode)
 * @property yearFrom Starting year of the movie/series
 * @property yearTo Ending year for series (null for movies)
 * @property poster URL to the movie poster image
 * @property genre List of genres
 * @property directors List of directors
 * @property writers List of writers
 * @property actors List of main actors
 * @property plot Movie plot summary
 * @property language List of languages
 * @property country List of production countries
 * @property imdbRating IMDB rating score (0-10)
 * @property seenAtInMillis Timestamp when the movie was last viewed
 * @property isFavourite Whether the movie is marked as favourite
 */
@Entity(tableName = "movieDetails")
data class Movie(
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val imdbId: String,
    val title: String,
    val type: MovieType,
    @SerializedName("year_from")
    val yearFrom: Int,
    @SerializedName("year_to")
    val yearTo: Int?,
    val poster: String,
    val genre: List<String>,
    val directors: List<String>,
    val writers: List<String>,
    val actors: List<String>,
    val plot: String,
    val language: List<String>,
    val country: List<String>,
    @SerializedName("rating")
    val imdbRating: Double?,
    var seenAtInMillis: Long,
    var isFavourite: Boolean = false,
)



