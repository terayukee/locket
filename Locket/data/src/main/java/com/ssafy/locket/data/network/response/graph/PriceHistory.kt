package com.ssafy.locket.data.network.response.graph

data class PriceHistory(
    val highestPrice: Int,
    val lowestPrice: Int,
    val priceDate: String
)