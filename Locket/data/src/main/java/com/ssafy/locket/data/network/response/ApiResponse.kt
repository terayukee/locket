package com.ssafy.locket.data.network.response

sealed class ApiResponse<out T> {
    data class Success<T>(val data: T): ApiResponse<T>()
    data class Error(val error: ErrorResponse): ApiResponse<Nothing>()
}