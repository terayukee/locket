package com.ssafy.locket.data.network.response

import android.util.Log
import com.ssafy.locket.model.ApiResponse
import retrofit2.Response

suspend fun <T> emitApiResponse(
    apiResponse: suspend () -> Response<T>,
    default: T,
): ApiResponse<T> =
    runCatching {
        val response = apiResponse()
        Log.d("emitApiResponse", "HTTP status: ${response.code()}, body: ${response.body()}")

        if (response.isSuccessful) {
            val result = response.body()
            if (result != null) {
                ApiResponse.Success(data = result)
            } else {
                ApiResponse.Error.UnknownError(message = "Response body is null")
            }
        } else {
            ApiResponse.Error.ServerError(
                code = response.code().toString(),
                message = response.message()
            )
        }
    }.getOrElse { e ->
        Log.e("emitApiResponse", "Exception occurred", e) // 전체 스택 트레이스 출력
        ApiResponse.Error.UnknownError(message = e.message ?: "Unknown Error")
    }