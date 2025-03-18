package com.ssafy.locket.data.remote.response

data class GetPaymentHistoryRes(
    val paymentHistory: List<PaymentHistory>,
    val user_id: Int
)