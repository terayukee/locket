package com.ssafy.locket.data.network.response.payment

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.payment.Payment
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentResponse(
    val transactionId: String,
    val status: String,
    val message: String
): BaseResponse {
    companion object: DataMapper<PaymentResponse, Payment> {
        override fun PaymentResponse.toDomainModel(): Payment {
            return Payment(transactionId, status, message)
        }
    }
}
