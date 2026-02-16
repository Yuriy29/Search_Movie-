package com.dopayurii.movie.data.remote

import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.data.model.MovieType
import com.google.gson.annotations.SerializedName

/**
 * Data class representing a single search result item from OMDb API.
 *
 * @property title Movie title
 * @property year Release year (may include range for series, e.g., "2011-2019")
 * @property imdbId Unique IMDB identifier
 * @property type Type of content (movie, series, episode)
 * @property poster URL to the movie poster image
 */
data class ResponseSearchItem(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("imdbID")
    val imdbId: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val poster: String,
)

/**
 * Converts this ResponseSearchItem to a MovieSummary domain model.
 * Parses the year string to extract the starting year.
 *
 * @return MovieSummary with parsed year and mapped type
 */
fun ResponseSearchItem.toMovieSummary() : MovieSummary {
    return MovieSummary(
        title, year.split("–").first().toInt(), imdbId, MovieType.getMovieType(type), poster
    )
}
