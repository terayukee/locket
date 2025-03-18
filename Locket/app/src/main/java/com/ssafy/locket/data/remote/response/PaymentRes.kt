package com.ssafy.locket.data.remote.response

data class PaymentRes(
    val status: String,
    val message: String,
    val data: String,
    val error: String
)