package com.ssafy.locket.model.payment_history

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class PaymentMonthlyHistoryItem(
    val cardName: String,
    val category: String,
    val storeName: String,
    val totalAmount: BigDecimal,
    val year: Int,
    val month: Int
): BaseModel
