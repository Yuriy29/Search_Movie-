package com.dopayurii.movie.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Data class representing the OMDb API search response.
 *
 * @property search List of search result items (can be null when response is "False")
 * @property totalResults Total number of results available as a string
 * @property response API response status ("True" or "False")
 * @property error Error message if response is "False"
 */
data class ResponseSearch(
    @SerializedName("Search")
    val search: List<ResponseSearchItem>?,
    val totalResults: String,
    @SerializedName("Response")
    val response: String,
    @SerializedName("Error")
    val error: String?
)

