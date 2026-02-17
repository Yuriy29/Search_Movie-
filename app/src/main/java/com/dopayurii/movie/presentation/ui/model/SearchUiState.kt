package com.dopayurii.movie.presentation.ui.model

import com.dopayurii.movie.data.model.MovieSummary

data class SearchUiState(
    val query: String = "",
    val searchResults: List<MovieSummary> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val totalResults: Int = 0,
    val currentPage: Int = 1,
    val hasMoreResults: Boolean = false,
)