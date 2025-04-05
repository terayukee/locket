package com.ssafy.locket.model.payment_history

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentCalendarItem (
    val amount: Int,
    val date: String
): BaseModel