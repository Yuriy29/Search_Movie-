package com.dopayurii.movie.data.model

/**
 * Data class representing a summary of a movie for list display.
 * Used in search results to show basic movie information.
 *
 * @property title Movie title
 * @property year Release year
 * @property imdbId Unique IMDB identifier
 * @property type Type of content (movie, series, episode)
 * @property poster URL to the movie poster image
 */
data class MovieSummary(
    val title: String,
    val year: Int,
    val imdbId: String,
    val type: MovieType,
    val poster: String,
)