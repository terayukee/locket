package com.ssafy.locket.data.remote.api

import com.ssafy.locket.data.remote.response.product.ProductListRes
import com.ssafy.locket.data.remote.response.product.RecommandProductListRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductService {
    @GET("products")
    suspend fun getProductList(): Response<ProductListRes>

    @GET("products/recommendations/{user_id}")
    suspend fun getRecommendProductList(@Path("user_id") user_id: Int): Response<RecommandProductListRes>
}