package com.ssafy.locket.data.remote.response

data class ProductInfo(
    val color: String,
    val current_price: Int,
    val discount_amount: Int,
    val discount_percentage: Double,
    val discount_rate: Int,
    val highest_price_6months: Int,
    val image_url: String,
    val model: String,
    val name: String,
    val review_count: Int,
    val review_rating: Double,
    val shipping_info: String
)