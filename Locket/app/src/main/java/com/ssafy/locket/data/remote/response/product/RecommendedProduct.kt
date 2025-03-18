package com.ssafy.locket.data.remote.response

data class RecommendedProduct(
    val category: String,
    val current_price: Int,
    val discount_amount: Int,
    val discount_percentage: Double,
    val highest_price_6months: Int,
    val image_url: String,
    val matching_reason: String,
    val name: String,
    val product_id: String,
    val review_count: Int,
    val review_rating: Double,
    val shipping_info: String
)