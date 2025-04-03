package com.ssafy.locket.data.network.response.graph

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.graph.ProductResponse.Companion.toDomainModel
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductCategoryListResponse(
    val category: Int,
    val categoryName: String,
    val page: Int,
    val pageProductCount: Int,
    val totalPages: Int,
    val categoryProductCount: Int,
    val products: List<ProductResponse>,
) : BaseResponse {
        companion object : DataMapper<ProductCategoryListResponse, ProductCategoryListInfo> {
        override fun ProductCategoryListResponse.toDomainModel(): ProductCategoryListInfo {
            return ProductCategoryListInfo(
                categoryName = this.categoryName,
                products = this.products.map { it.toDomainModel() }
            )
        }
    }
}