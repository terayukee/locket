package com.ssafy.locket.data.network.response.payment_history

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.payment_history.PaymentHistoryItem
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class PaymentMonthlyHistoryResponseItem(
    val cardName: String,
    val paymentCategory: String,
    val storeName: String,
    val totalAmount: BigDecimal,
    val year: Int,
    val month: Int
): BaseResponse {
    companion object: DataMapper<PaymentMonthlyHistoryResponseItem, PaymentHistoryItem> {
        override fun PaymentMonthlyHistoryResponseItem.toDomainModel(): PaymentHistoryItem {
            return PaymentHistoryItem(
                cardName = cardName,
                category = paymentCategory,
                storeName = storeName,
                totalAmount = totalAmount,
                year = year,
                month = month
            )
        }
    }
}