package com.ssafy.locket.model.payment_history

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentCalendar(
    val dailySpending: List<PaymentCalendarItem>,
    val monthlyTotal: Int
): BaseModel
