package com.ssafy.locket.model.home

data class ReceiptDetail(
    val itemId: Long,
    val itemAmount: Int,
    val itemCategory: String,
    val itemName: String,
    val itemQuantity: Int
)