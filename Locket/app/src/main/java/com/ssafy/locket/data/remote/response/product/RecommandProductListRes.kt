package com.ssafy.locket.data.remote.response.product

data class RecommandProductListRes(
    val message: String,
    val recommended_products: List<RecommendedProduct>,
    val status: Int,
    val user_id: String
)