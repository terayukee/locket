package com.ssafy.locket.model.payment

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentCardList (
    val cards: List<PaymentCard>,
    val count: Int
): BaseModel