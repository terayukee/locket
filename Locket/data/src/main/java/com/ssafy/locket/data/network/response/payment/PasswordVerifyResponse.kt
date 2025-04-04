package com.ssafy.locket.data.network.response.payment

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.request.payment.PassswordVerifyRequest
import com.ssafy.locket.model.payment.PasswordVerify
import kotlinx.parcelize.Parcelize

@Parcelize
data class PasswordVerifyResponse(
    val userId: Long,
    val valid: Boolean
): BaseResponse {
    companion object: DataMapper<PasswordVerifyResponse, PasswordVerify> {
        override fun PasswordVerifyResponse.toDomainModel(): PasswordVerify {
            return PasswordVerify(
                userId = userId,
                valid = valid
            )
        }
    }
}
