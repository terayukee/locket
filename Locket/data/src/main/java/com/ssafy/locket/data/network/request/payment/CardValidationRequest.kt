package com.ssafy.locket.data.network.request.payment

import java.math.BigDecimal

data class CardValidationRequest(
    val cardId: Int,
    val amount: BigDecimal
)
