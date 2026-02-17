package com.dopayurii.movie.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dopayurii.movie.BuildConfig
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.data.remote.MovieApiService
import com.dopayurii.movie.data.remote.toMovieSummary
import javax.inject.Inject

class SearchMoviesPagingSource @Inject constructor(
    private val apiService: MovieApiService,
    private val query: String
) : PagingSource<Int, MovieSummary>() {

    companion object {
        private const val TAG = "SearchPagingSource"
        private const val STARTING_PAGE_INDEX = 1
        private const val MIN_QUERY_LENGTH = 2

        /** Page size for pagination - shared across app */
        const val PAGE_SIZE = 10
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieSummary> {
        val page = params.key ?: STARTING_PAGE_INDEX

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Loading page $page for query: '$query'")
        }

        if (query.length < MIN_QUERY_LENGTH) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Query too short, returning empty")
            }
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        return try {
            val response = apiService.searchMovies(search = query.trim(), page = page)
            val searchList = response.search ?: emptyList()

            if (response.response == "False" || searchList.isEmpty()) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "API returned False or empty: ${response.error}")
                }
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }

            val movies = searchList.map { it.toMovieSummary() }
                .distinctBy { it.imdbId }

            val totalResults = response.totalResults.toIntOrNull() ?: 0
            val loadedSoFar = (page - 1) * PAGE_SIZE + movies.size
            val nextKey = if (loadedSoFar >= totalResults || movies.isEmpty()) null else page + 1

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Returning ${movies.size} movies, page=$page, nextKey=$nextKey, loaded=$loadedSoFar/$totalResults")
            }

            LoadResult.Page(
                data = movies,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = nextKey
            )

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "Error loading page $page", e)
            }
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieSummary>): Int? {
        return state.anchorPosition
    }
}
