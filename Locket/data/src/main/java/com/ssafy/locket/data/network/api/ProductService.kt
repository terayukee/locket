package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.request.product.ProductAlertRequest
import com.ssafy.locket.data.network.request.product.ProductLikeRequest
import com.ssafy.locket.data.network.response.graph.PriceHappinessResponse
import com.ssafy.locket.data.network.response.graph.ProductCategoryListResponse
import com.ssafy.locket.data.network.response.graph.ProductDetailResponse
import com.ssafy.locket.data.network.response.graph.ProductLikeListResponse
import com.ssafy.locket.data.network.response.graph.ProductLikeResponse
import com.ssafy.locket.data.network.response.graph.ProductSearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductService {
    @POST("users/products/{productId}/like")
    suspend fun addToFavorites(@Path("productId") productId: Int,@Body productLikeRequest: ProductLikeRequest) : Response<ProductLikeResponse>

    @POST("users/products/{productId}/alert")
    suspend fun addAlarm(@Path("productId") productId: Int,@Body productAlertRequest: ProductAlertRequest) : Response<ProductLikeResponse>

    @GET("users/products")
    suspend fun getCategoryList(@Query("category") category: Int,@Query("page") page: Int) :Response<ProductCategoryListResponse>

    @GET("users/products/{productId}")
    suspend fun getDetailProductInfo(@Path("productId") productId: Int,@Query("userId") userId: Int) : Response<ProductDetailResponse>

    @GET("users/products/liked")
    suspend fun getLikeProductList(@Query("userId") userId:Int,@Query("page") page:Int) :Response<ProductLikeListResponse>

    @GET("users/products/happiness")
    suspend fun getHappiness() :Response<PriceHappinessResponse>

    @GET("users/products/search")
    suspend fun getSearchProduct(@Query("product_name") product_name: String,@Query("page") page: Int) :Response<ProductSearchResponse>
}