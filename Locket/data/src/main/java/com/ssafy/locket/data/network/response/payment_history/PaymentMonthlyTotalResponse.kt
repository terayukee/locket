package com.ssafy.locket.data.network.response.payment_history

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.payment_history.PaymentMonthlyTotal
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class PaymentMonthlyTotalResponse(
    val userId: Long,
    val year: Int,
    val totalAmount: BigDecimal,
    val month: Int
): BaseResponse {
    companion object: DataMapper<PaymentMonthlyTotalResponse, PaymentMonthlyTotal> {
        override fun PaymentMonthlyTotalResponse.toDomainModel(): PaymentMonthlyTotal {
            return PaymentMonthlyTotal(
                totalAmount = totalAmount
            )
        }
    }
}
