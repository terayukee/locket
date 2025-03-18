package com.ssafy.locket.data.remote.response

data class PaymentHistory(
    val amount: Double,
    val currency: String,
    val merchant: String,
    val payment_timestamp: String,
    val payment_transaction_id: String,
    val status: String
)