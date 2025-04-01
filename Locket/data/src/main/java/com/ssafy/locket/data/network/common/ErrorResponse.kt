package com.ssafy.locket.data.network.common

import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.base.NetworkError
import kotlinx.parcelize.Parcelize

@Parcelize
class ErrorResponse(
    val timestamp   : String? = null,
    val status      : String? = null,
    val error       : String? = null,
    val code        : String? = null,
    val message     : String? = null,
) : BaseResponse {
    companion object: DataMapper<ErrorResponse, NetworkError> {
        override fun ErrorResponse.toDomainModel(): NetworkError {
            return NetworkError(
                error = error ?: "NO_ERROR",
                code = code ?: "401",
                status  = status ?: "NO_STATUS",
                message = message ?: "알 수 없는 에러"
            )
        }
    }
}