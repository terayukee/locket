package com.ssafy.locket.data.network.response.finance.payment_history

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.finance.payment_history.PaymentDailyHistoryItem
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class PaymentDailyHistoryResponseItem(
    val id: String,
    val day: Int,
    val cardName: String,
    val paymentCategory: String,
    val storeName: String,
    val totalAmount: BigDecimal
): BaseResponse {
    companion object: DataMapper<PaymentDailyHistoryResponseItem, PaymentDailyHistoryItem> {
        override fun PaymentDailyHistoryResponseItem.toDomainModel(): PaymentDailyHistoryItem {
            return PaymentDailyHistoryItem(
                id = id,
                cardName = cardName,
                category = paymentCategory,
                storeName = storeName,
                totalAmount = totalAmount
            )
        }
    }
}