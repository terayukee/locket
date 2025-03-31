package com.ssafy.locket.model.home

data class Receipt(
    val amount: Int,
    val cardName: String,
    val paymentCategory: String,
    val paymentDate: String,
    val storeName: String,
    val transactionId: Int
)