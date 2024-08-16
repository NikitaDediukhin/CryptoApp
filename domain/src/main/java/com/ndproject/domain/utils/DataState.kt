package com.ndproject.domain.utils

sealed class DataState<out T> {
    object Loading : DataState<Nothing>()
    data class Success<out T>(val data: T) : DataState<T>()
    data class Error(val message: String) : DataState<Nothing>()
}