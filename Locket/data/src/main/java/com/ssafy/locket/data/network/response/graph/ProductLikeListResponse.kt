package com.ssafy.locket.data.network.response.graph

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.graph.ProductResponse.Companion.toDomainModel
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import com.ssafy.locket.model.graph.ProductLikeListInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductLikeListResponse(
    val likedProductCount: Int,
    val page: Int,
    val totalLikedProducts: Int,
    val totalPages: Int,
    val userId: Int,
    val likedProducts: List<ProductResponse>,
): BaseResponse {
    companion object : DataMapper< ProductLikeListResponse, ProductLikeListInfo> {
        override fun  ProductLikeListResponse.toDomainModel(): ProductLikeListInfo {
            return ProductLikeListInfo(
                products = this.likedProducts.map { it.toDomainModel() }
            )
        }
    }
}