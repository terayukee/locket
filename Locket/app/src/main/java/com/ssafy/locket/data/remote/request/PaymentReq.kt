package com.ssafy.locket.data.remote.request

data class PaymentReq(
    val account_id: Int,
    val amount: Double,
    val card_id: Int,
    val currency: String,
    val merchant_id: Int,
    val user_id: Int
)