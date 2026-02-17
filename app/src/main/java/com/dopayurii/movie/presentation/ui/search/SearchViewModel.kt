package com.dopayurii.movie.presentation.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dopayurii.movie.BuildConfig
import com.dopayurii.movie.data.model.MovieSummary
import com.dopayurii.movie.domain.usecase.SearchMoviesUseCase
import com.dopayurii.movie.presentation.ui.model.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
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
    data object OnClearSearch : SearchUiEvent()
}

/**
 * ViewModel for the Search screen using Paging 3 for pagination.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMovies: SearchMoviesUseCase
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_MS = 300L
        private const val MIN_QUERY_LENGTH = 2
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    // Debounced search flow - triggers search 300ms after user stops typing
    // Using extraBufferCapacity to ensure emissions don't fail
    private val searchQueryFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        replay = 0
    )

    /**
     * PagingData flow for search results.
     * Uses flatMapLatest to cancel previous search when query changes.
     * Cached in viewModelScope to survive configuration changes.
     */
    val searchResults: Flow<PagingData<MovieSummary>> = searchQueryFlow
        .filter { it.length >= MIN_QUERY_LENGTH } // Filter first to prevent short queries
        .debounce(DEBOUNCE_MS)
        .distinctUntilChanged() // Prevent duplicate queries
        .flatMapLatest { query ->
            if (BuildConfig.DEBUG) {
                Log.d("SearchViewModel", "flatMapLatest executing search for: '$query'")
            }
            searchMovies(query)
        }
        .cachedIn(viewModelScope)

    init {
        // Update UI state when search is triggered
        searchQueryFlow
            .filter { it.length >= MIN_QUERY_LENGTH } // Filter first
            .debounce(DEBOUNCE_MS)
            .onEach { query ->
                if (BuildConfig.DEBUG) {
                    Log.d("SearchViewModel", "onEach: updating UI state for query: '$query'")
                }
                _uiState.update { it.copy(query = query, isLoading = true, errorMessage = null) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: SearchUiEvent) {
        when (event) {
            is SearchUiEvent.OnQueryChange -> {
                if (BuildConfig.DEBUG) {
                    Log.d("SearchViewModel", "onEvent OnQueryChange: '${event.query}'")
                }
                _uiState.update { it.copy(query = event.query) }
                // Only emit to search flow if query meets minimum length
                if (event.query.length >= MIN_QUERY_LENGTH) {
                    viewModelScope.launch {
                        if (BuildConfig.DEBUG) {
                            Log.d("SearchViewModel", "Emitting query: '${event.query}'")
                        }
                        searchQueryFlow.emit(event.query)
                    }
                }
            }

            is SearchUiEvent.OnClearSearch -> {
                if (BuildConfig.DEBUG) {
                    Log.d("SearchViewModel", "onEvent OnClearSearch")
                }
                _uiState.update {
                    SearchUiState() // Reset to initial state
                }
                // Don't emit to searchQueryFlow - just let UI reset via SearchScreen logic
            }
        }
    }
}
