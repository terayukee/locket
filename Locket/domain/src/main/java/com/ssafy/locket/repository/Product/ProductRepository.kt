package com.ssafy.locket.repository.Product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import com.ssafy.locket.model.graph.ProductHappyListInfo
import com.ssafy.locket.model.graph.ProductLikeListInfo
import com.ssafy.locket.model.graph.ProductxInfo
import com.ssafy.locket.model.graph.product_detail.ProductDetailInfo
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun addToFavorites(productId: Int) : Flow<ResponseStatus<ProductxInfo>>
    suspend fun addAlarm(productId: Int) : Flow<ResponseStatus<ProductxInfo>>
    suspend fun getCategoryList(category: Int,page: Int) :Flow<ResponseStatus<ProductCategoryListInfo>>
    suspend fun getDetailProductInfo(productId: Int,userId: Int) : Flow<ResponseStatus<ProductDetailInfo>>
    suspend fun getLikeProductList(userId:Int) :Flow<ResponseStatus<ProductLikeListInfo>>
    suspend fun getHappyProductList() : Flow<ResponseStatus<ProductHappyListInfo>>
}