package com.ssafy.locket.model.finance.payment_history

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class PaymentDailyHistoryItem(
    val id: String,
    val cardName: String,
    val category: String?,
    val storeName: String,
    val totalAmount: BigDecimal
): BaseModel
