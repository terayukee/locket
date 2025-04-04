package com.ssafy.locket.data.network.response.graph

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.graph.ProductResponse.Companion.toDomainModel
import com.ssafy.locket.model.graph.ProductHappyListInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceHappinessResponse(
    val category: Int,
    val categoryName: String,
    val categoryProductCount: Int,
    val page: Int,
    val pageProductCount: Int,
    val products: List<ProductResponse>,
    val totalPages: Int
) : BaseResponse {
    companion object : DataMapper<PriceHappinessResponse, ProductHappyListInfo> {
        override fun PriceHappinessResponse.toDomainModel(): ProductHappyListInfo {
            return ProductHappyListInfo(
                products = this.products.map { it.toDomainModel() }
            )
        }
    }
}