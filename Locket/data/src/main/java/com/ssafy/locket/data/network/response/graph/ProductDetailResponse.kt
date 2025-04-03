package com.ssafy.locket.data.network.response.graph

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.graph.PriceHistoryResponse.Companion.toDomainModel
import com.ssafy.locket.model.graph.product_detail.ProductDetailInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDetailResponse(
    val alert: Boolean,
    val alertPrice: Int,
    val averagePrice: Int,
    val coupangUrl: String,
    val currentPrice: String,
    val discountAmount: String,
    val discountRate: String,
    val highestPrice: String,
    val imageUrl: String,
    val liked: Boolean,
    val priceHistory: List<PriceHistoryResponse>,
    val productId: Int,
    val productName: String,
    val reviewCount: String,
    val reviewRating: String,
    val shippingType: String,
    val unitPrice: String,
    val userId: Int
): BaseResponse {
    companion object : DataMapper<ProductDetailResponse, ProductDetailInfo> {
        override fun ProductDetailResponse.toDomainModel(): ProductDetailInfo {
            return ProductDetailInfo(
                alert = this.alert,
                alertPrice = this.alertPrice,
                averagePrice = this.averagePrice,
                coupangUrl = this.coupangUrl,
                currentPrice = this.currentPrice,
                discountAmount = this.discountAmount,
                discountRate = this.discountRate,
                highestPrice = this.highestPrice,
                imageUrl = this.imageUrl,
                liked = this.liked,
                priceHistory = this.priceHistory.map { it.toDomainModel() },
                productId = this.productId,
                productName = this.productName,
                reviewCount = this.reviewCount,
                reviewRating = this.reviewRating,
                shippingType = this.shippingType,
                unitPrice = this.unitPrice,
                userId = this.userId
            )
        }
    }
}