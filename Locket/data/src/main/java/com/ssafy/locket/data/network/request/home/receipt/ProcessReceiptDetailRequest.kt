package com.ssafy.locket.data.network.request.home.receipt

data class ProcessReceiptDetailRequest(
    val itemId: Long,
    val itemAmount: Int,
    val itemCategory: String?,
    val itemName: String,
    val itemQuantity: Int
)
