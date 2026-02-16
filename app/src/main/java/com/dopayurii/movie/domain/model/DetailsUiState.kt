package com.dopayurii.movie.domain.model

import com.dopayurii.movie.data.model.Movie

data class DetailsUiState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)