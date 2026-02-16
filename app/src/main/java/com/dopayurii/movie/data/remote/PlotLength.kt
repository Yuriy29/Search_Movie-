package com.dopayurii.movie.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Enum class representing the length of plot summary to request from OMDb API.
 */
enum class PlotLength {
    @SerializedName("short") SHORT,
    @SerializedName("full") FULL
}
