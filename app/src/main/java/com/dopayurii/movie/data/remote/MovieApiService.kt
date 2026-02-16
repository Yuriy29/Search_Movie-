package com.dopayurii.movie.data.remote

import com.dopayurii.movie.BuildConfig.API_KEY
import com.dopayurii.movie.data.model.MovieType
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit service interface for OMDb API operations.
 * Provides endpoints for searching movies and fetching detailed movie information.
 */
interface MovieApiService {

    /**
     * Searches for movies by query string with optional filters.
     *
     * @param search Search query string (minimum 2 characters)
     * @param year Optional year filter
     * @param page Page number for pagination (1-100)
     * @param type Optional type filter (movie, series, episode)
     * @param apiKey OMDb API key (defaults to BuildConfig.API_KEY)
     * @return ResponseSearch containing search results and metadata
     */
    @GET("/")
    suspend fun searchMovies(
        @Query("s") search: String,
        @Query("y") year: Int? = null,
        @Query("page") page: Int? = null,
        @Query("type") type: MovieType? = null,
        @Query("apikey") apiKey: String = API_KEY
    ) : ResponseSearch

    /**
     * Fetches detailed movie information by IMDB ID.
     *
     * @param id IMDB ID of the movie (e.g., "tt0111161")
     * @param year Optional year filter
     * @param type Optional type filter (movie, series, episode)
     * @param plotLength Length of plot summary (short or full)
     * @param apiKey OMDb API key (defaults to BuildConfig.API_KEY)
     * @return ResponseMovie containing detailed movie information
     */
    @GET("/")
    suspend fun getById(
        @Query("i") id: String,
        @Query("y") year: Int? = null,
        @Query("type") type: MovieType? = null,
        @Query("plot") plotLength: PlotLength? = PlotLength.FULL,
        @Query("apikey") apiKey: String = API_KEY
    ) : ResponseMovie
}
