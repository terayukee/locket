package com.ssafy.locket.model.finance.payment_history

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentDailyHistory (
    val list: List<PaymentDailyHistoryItem>
): BaseModel