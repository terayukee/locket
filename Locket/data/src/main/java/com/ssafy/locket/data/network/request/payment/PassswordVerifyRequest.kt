package com.ssafy.locket.data.network.request.payment

data class PassswordVerifyRequest(
    val userId: Long,
    val paymentPassword: Int
)
