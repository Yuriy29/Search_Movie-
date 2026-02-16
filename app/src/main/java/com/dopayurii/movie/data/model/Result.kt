package com.dopayurii.movie.data.model

/**
 * Sealed class representing the result of an operation that can either succeed with data,
 * fail with an error message, or be in a loading state.
 *
 * @param T The type of data returned on success
 */
sealed class Result<out T> {
    /**
     * Represents a successful result containing data.
     *
     * @property data The successful result data
     */
    data class Success<T>(val data: T) : Result<T>()

    /**
     * Represents a failed result with an error message and optional exception.
     *
     * @property message Human-readable error description
     * @property exception Optional underlying exception that caused the error
     */
    data class Error(val message: String, val exception: Throwable? = null) : Result<Nothing>()

    /**
     * Represents a loading state while an operation is in progress.
     */
    data object Loading : Result<Nothing>()
}