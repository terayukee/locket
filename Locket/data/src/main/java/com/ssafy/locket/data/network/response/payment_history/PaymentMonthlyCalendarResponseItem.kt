package com.ssafy.locket.data.network.response.payment_history

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.payment_history.PaymentCalendarItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMonthlyCalendarResponseItem(
    val amount: Int,
    val date: String
): BaseResponse {
    companion object: DataMapper<PaymentMonthlyCalendarResponseItem, PaymentCalendarItem> {
        override fun PaymentMonthlyCalendarResponseItem.toDomainModel(): PaymentCalendarItem {
            return PaymentCalendarItem(
                amount = this.amount,
                date = this.date
            )
        }
    }
}