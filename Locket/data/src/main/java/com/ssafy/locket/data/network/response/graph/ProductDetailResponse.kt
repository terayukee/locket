package com.ssafy.locket.data.network.response.graph

data class ProductDetailResponse(
    val alert: Boolean,
    val alertPrice: Int,
    val averagePrice: Int,
    val coupangUrl: String,
    val currentPrice: String,
    val discountAmount: String,
    val discountRate: String,
    val highestPrice: String,
    val imageUrl: String,
    val liked: Boolean,
    val priceHistory: List<PriceHistory>,
    val productId: Int,
    val productName: String,
    val reviewCount: String,
    val reviewRating: String,
    val shippingType: String,
    val unitPrice: String,
    val userId: Int
)