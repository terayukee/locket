package com.ssafy.locket.data.network.response.finance.payment_history

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyCalendarResponseItem.Companion.toDomainModel
import com.ssafy.locket.model.payment_history.PaymentCalendar
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMonthlyCalendarResponse(
    @SerializedName("dailySpending") val dailySpending: List<PaymentMonthlyCalendarResponseItem>,
    val monthlyTotal: Int
): BaseResponse {
    companion object: DataMapper<PaymentMonthlyCalendarResponse, PaymentCalendar> {
        override fun PaymentMonthlyCalendarResponse.toDomainModel(): PaymentCalendar {
            return PaymentCalendar(
                dailySpending = this.dailySpending.map { it.toDomainModel() }?: emptyList(),
                monthlyTotal = this.monthlyTotal
            )
        }
    }
}