package com.ssafy.locket.data.network.response.payment_history

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.payment_history.PaymentDailyHistoryItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentDailyHistoryResponseItem(
    val cardName: String,
    val paymentCategory: String,
    val storeName: String,
    val totalAmount: Int
): BaseResponse {
    companion object: DataMapper<PaymentDailyHistoryResponseItem, PaymentDailyHistoryItem> {
        override fun PaymentDailyHistoryResponseItem.toDomainModel(): PaymentDailyHistoryItem {
            return PaymentDailyHistoryItem(
                cardName = cardName,
                category = paymentCategory,
                storeName = storeName,
                totalAmount = totalAmount
            )
        }
    }
}