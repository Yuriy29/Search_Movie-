package com.dopayurii.movie.data.remote

import com.dopayurii.movie.data.model.Movie
import com.dopayurii.movie.data.model.MovieType
import com.google.gson.annotations.SerializedName

/**
 * Data class representing detailed movie information from OMDb API.
 * Contains comprehensive movie data including cast, crew, plot, and ratings.
 *
 * @property title Movie title
 * @property year Release year (may include range for series)
 * @property genre Comma-separated list of genres
 * @property directors Comma-separated list of directors
 * @property writer Comma-separated list of writers
 * @property actors Comma-separated list of actors
 * @property plot Movie plot summary
 * @property language Comma-separated list of languages
 * @property country Comma-separated list of production countries
 * @property poster URL to the movie poster image
 * @property imdbRating IMDB rating as a string
 * @property imdbId Unique IMDB identifier
 * @property type Type of content (movie, series, episode)
 */
class ResponseMovie(
    @SerializedName("Title")
    val title: String,
    @SerializedName("Year")
    val year: String,
    @SerializedName("Genre")
    val genre: String,
    @SerializedName("Director")
    val directors: String,
    @SerializedName("Writer")
    val writer: String,
    @SerializedName("Actors")
    val actors: String,
    @SerializedName("Plot")
    val plot: String,
    @SerializedName("Language")
    val language: String,
    @SerializedName("Country")
    val country: String,
    @SerializedName("Poster")
    val poster: String,
    @SerializedName("imdbRating")
    val imdbRating: String,
    @SerializedName("imdbID")
    val imdbId: String,
    @SerializedName("Type")
    val type: String,
) {
    /**
     * Converts this ResponseMovie to a Movie domain model.
     * Parses comma-separated strings into lists and extracts year range.
     *
     * @return Movie entity with all details properly parsed
     */
    fun toMovie() : Movie {
        return Movie(
            imdbId = imdbId,
            title = title,
            type = getMovieType(),
            yearFrom = getYearFrom(),
            yearTo = getYearTo(),
            poster = poster,
            genre = genre.toList(),
            directors = directors.toList(),
            writers = writer.toList(),
            actors = actors.toList(),
            plot = plot,
            language = language.toList(),
            country = country.toList(),
            imdbRating = imdbRating.toDoubleOrNull(),
            seenAtInMillis = System.currentTimeMillis(),
            isFavourite = false,
        )
    }

    private fun getMovieType() : MovieType {
        return MovieType.getMovieType(type)
    }

    private fun getYearFrom() : Int{
        return year.split("–").firstOrNull()?.toInt() ?: 2000
    }

    private fun getYearTo() : Int?{
        return year.split("–").getOrNull(1)?.toIntOrNull()
    }

    private fun String.toList() : List<String>{
        return this.split(",").map { it.trim() }
    }

}
