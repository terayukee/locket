package com.ssafy.locket.repository.Product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import com.ssafy.locket.model.graph.ProductHappyListInfo
import com.ssafy.locket.model.graph.ProductLikeListInfo
import com.ssafy.locket.model.graph.ProductSearchInfo
import com.ssafy.locket.model.graph.ProductxInfo
import com.ssafy.locket.model.graph.product_detail.ProductDetailInfo
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun addToFavorites(productId: Int,isLiked :Boolean) : Flow<ResponseStatus<Unit>>
    suspend fun addAlarm(productId: Int,isAlert: Boolean,alertPrice: Int) : Flow<ResponseStatus<Unit>>
    suspend fun getCategoryList(category: Int,page: Int) :Flow<ResponseStatus<ProductCategoryListInfo>>
    suspend fun getDetailProductInfo(productId: Int,userId: Int) : Flow<ResponseStatus<ProductDetailInfo>>
    suspend fun getLikeProductList(userId:Int,page:Int) :Flow<ResponseStatus<ProductLikeListInfo>>
    suspend fun getHappyProductList() : Flow<ResponseStatus<ProductHappyListInfo>>
    suspend fun getSearchProductList(product_name: String,page: Int) : Flow<ResponseStatus<ProductSearchInfo>>
}