package com.dopayurii.movie.presentation.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dopayurii.movie.data.model.Result
import com.dopayurii.movie.presentation.ui.model.DetailsUiState
import com.dopayurii.movie.domain.usecase.FetchMovieDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Movie Details screen.
 */
@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val fetchMovieDetails: FetchMovieDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailsUiState())
    val uiState: StateFlow<DetailsUiState> = _uiState.asStateFlow()

    fun loadMovie(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            when (val result = fetchMovieDetails(id)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            movie = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is Result.Loading -> { /* Already handled */ }
            }
        }
    }
}
