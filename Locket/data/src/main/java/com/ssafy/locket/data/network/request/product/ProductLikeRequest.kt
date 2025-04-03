package com.ssafy.locket.data.network.request.product

data class ProductLikeRequest(
    val isLiked: Boolean,
    val userId: Int
)