package com.dopayurii.movie.presentation.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dopayurii.movie.data.model.Result
import com.dopayurii.movie.domain.model.SearchUiState
import com.dopayurii.movie.domain.usecase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Events that can be triggered from the Search screen.
 */
sealed class SearchUiEvent {
    data class OnQueryChange(val query: String) : SearchUiEvent()
    data class OnSearchSubmit(val query: String) : SearchUiEvent()
    data object OnLoadMore : SearchUiEvent()
    data object OnClearSearch : SearchUiEvent()
}

/**
 * ViewModel for the Search screen with pagination support.
 */
@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovies: SearchMoviesUseCase
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_MS = 750L
        private const val MIN_QUERY_LENGTH = 3
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var lastQuery = ""

    // Debounced search flow - triggers search 750ms after user stops typing
    private val searchQueryFlow = MutableSharedFlow<String>()

    init {
        searchQueryFlow
            .debounce(DEBOUNCE_MS)            // Wait after last emission
            .filter { it.length >= MIN_QUERY_LENGTH }  // Only search if query valid
            .onEach { query ->
                performSearch(query, page = 1)
            }
            .launchIn(viewModelScope)         // Scope to ViewModel lifecycle
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnQueryChange -> {
                _uiState.update { it.copy(query = event.query) }
                searchQueryFlow.tryEmit(event.query)  // Emit to debounced flow
            }

            is SearchUiEvent.OnSearchSubmit -> {
                performSearch(event.query, page = 1)  // Immediate search on submit
            }

            is SearchUiEvent.OnLoadMore -> {
                loadNextPage()
            }

            is SearchUiEvent.OnClearSearch -> {
                _uiState.update {
                    SearchUiState() // Reset to initial state
                }
                lastQuery = ""
            }
        }
    }

    private fun performSearch(query: String, page: Int) {
        if (query.length < MIN_QUERY_LENGTH) return

        lastQuery = query
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = searchMovies(query, page = page)) {
                is Result.Success -> {
                    val movies = result.data.movies
                    val total = result.data.totalResults
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            searchResults = movies,
                            totalResults = total,
                            currentPage = 1,
                            hasMoreResults = movies.size < total,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }

    private fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMoreResults) return

        val nextPage = currentState.currentPage + 1
        _uiState.update { it.copy(isLoadingMore = true) }

        viewModelScope.launch {
            when (val result = searchMovies(lastQuery, page = nextPage)) {
                is Result.Success -> {
                    val newMovies = result.data.movies
                    val allMovies = currentState.searchResults + newMovies
                    _uiState.update { state ->
                        state.copy(
                            isLoadingMore = false,
                            searchResults = allMovies,
                            currentPage = nextPage,
                            hasMoreResults = allMovies.size < state.totalResults,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update { state ->
                        state.copy(
                            isLoadingMore = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }
}
