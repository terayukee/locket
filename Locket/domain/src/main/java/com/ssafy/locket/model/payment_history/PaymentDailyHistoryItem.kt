package com.ssafy.locket.model.payment_history

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentDailyHistoryItem(
    val cardName: String,
    val category: String,
    val storeName: String,
    val totalAmount: Int
): BaseModel
