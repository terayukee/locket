package com.ssafy.locket.data.remote.response

data class Data(
    val page: Int,
    val products: List<Product>,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)