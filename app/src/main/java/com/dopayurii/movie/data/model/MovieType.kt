package com.dopayurii.movie.data.model

import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

/**
 * Enum class representing the type of movie content.
 * Maps to the Type field from OMDb API responses.
 */
enum class MovieType {
    @SerializedName("movie") MOVIE,
    @SerializedName("series") SERIES,
    @SerializedName("episode") EPISODE;

    companion object {
        /**
         * Converts a string representation to the corresponding MovieType enum.
         * Defaults to MOVIE if the string doesn't match known types.
         *
         * @param movieType String representation ("movie", "series", "episode")
         * @return Corresponding MovieType enum value
         */
        fun getMovieType(movieType: String): MovieType {
            return when(movieType) {
                "series" -> SERIES
                "episode" -> EPISODE
                else -> MOVIE
            }
        }
    }

    /**
     * Returns a color associated with this movie type for UI display.
     * @return Compose Color value
     */
    fun movieTypeColor() : Color {
        return when(this) {
            MOVIE-> Color(0xff64B5F6)//Color.Companion.Blue
            SERIES -> Color(0xffFFB74D)//Color.Companion.Red
            EPISODE -> Color(0xff81C784)//Color.Companion.Gray
        }
    }

    /**
     * Returns a user-friendly string representation of the movie type.
     * @return Formatted type name ("Movie", "Series", or "Episode")
     */
    override fun toString(): String {
        return when(this) {
            MOVIE-> "Movie"
            SERIES -> "Series"
            EPISODE -> "Episode"
        }
    }
}