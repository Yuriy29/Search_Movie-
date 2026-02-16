package com.dopayurii.movie.data.model

/**
 * Data class representing the result of a movie search operation.
 * Contains the list of movies and pagination information.
 *
 * @property movies List of movie summaries returned by the search
 * @property totalResults Total number of results available across all pages
 */
data class MovieSearchResult(
    val movies: List<MovieSummary> = emptyList(),
    val totalResults: Int = 0,
)