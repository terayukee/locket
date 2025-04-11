package com.ssafy.locket.data.network.request.product

data class ProductAlertRequest(
    val alertPrice: Int,
    val isAlert: Boolean,
    val userId: Int
)