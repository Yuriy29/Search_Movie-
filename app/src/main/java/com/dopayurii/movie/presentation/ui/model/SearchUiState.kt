package com.dopayurii.movie.presentation.ui.model

/**
 * UI state for the Search screen.
 * Note: Search results are now managed by Paging 3 (LazyPagingItems),
 * so this state only tracks query and high-level loading/error states.
 */
data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
