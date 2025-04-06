package com.ssafy.locket.model.payment_history

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class PaymentMonthlyTotal(
    val totalAmount: BigDecimal
): BaseModel
