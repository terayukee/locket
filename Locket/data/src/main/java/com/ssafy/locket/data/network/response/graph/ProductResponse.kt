package com.ssafy.locket.data.network.response.graph

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.graph.Product
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductResponse(
    val currentPrice: String,
    val discountRate: String,
    val imageUrl: String,
    val productId: Int,
    val productName: String
) : BaseResponse {
    companion object: DataMapper<ProductResponse, Product> {
        override fun ProductResponse.toDomainModel(): Product {
            return Product(
                currentPrice = this.currentPrice,
                discountRate = this.discountRate,
                imageUrl = this.imageUrl,
                productId = this.productId,
                productName = this.productName
            )
        }
    }
}