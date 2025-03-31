package com.ssafy.locket.model.base

sealed class ResponseStatus<out T> {
    data class Success<T>(val data: T): ResponseStatus<T>()
    data class Error(val error: NetworkError): ResponseStatus<Nothing>()
}