package com.ssafy.locket.model.finance.payment_history

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class PaymentCalendarItem (
    val amount: BigDecimal,
    val date: String
): BaseModel