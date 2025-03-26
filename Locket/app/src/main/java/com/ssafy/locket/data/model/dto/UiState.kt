package com.ssafy.locket.data.model.dto

sealed class UiState {
    data class Success<T>(val data: T): UiState()
    data class Error(val error: Throwable?): UiState()
    object Loading: UiState()
}
