package com.ssafy.locket.data.network.request.payment

import java.math.BigDecimal

data class PaymentRequest(
    val amount: BigDecimal,
    val cardId: Int,
    val paymentCategory: String,
    val paymentKey: String,
    val paymentMerchant: String,
    val sellerId: Long,
    val storeName: String
)