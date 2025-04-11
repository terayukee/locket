package com.ssafy.locket.model.finance.payment_history

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMonthlyHistory(
    val list: List<PaymentHistoryItem>
): BaseModel
