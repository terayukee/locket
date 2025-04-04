package com.ssafy.locket.data.network.response.graph

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.graph.ProductResponse.Companion.toDomainModel
import com.ssafy.locket.model.graph.ProductSearchInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductSearchResponse(
    val page: Int,
    val pageProductCount: Int,
    val products: List<ProductResponse>,
    val searchKeyword: String,
    val searchProductCount: Int,
    val totalPages: Int
): BaseResponse {
    companion object : DataMapper<ProductSearchResponse, ProductSearchInfo> {
        override fun ProductSearchResponse.toDomainModel(): ProductSearchInfo {
            return ProductSearchInfo(
                page = this.page,
                pageProductCount = this.pageProductCount,
                products = this.products.map { it.toDomainModel() },
                searchKeyword = this.searchKeyword,
                searchProductCount = this.searchProductCount,
                totalPages = this.totalPages
            )
        }
    }
}