package com.ssafy.locket.model.graph.product_detail

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductDetailInfo(
    val alertPrice: Int,
    val averagePrice: Int,
    val coupangUrl: String,
    val currentPrice: String,
    val discountAmount: String,
    val discountRate: String,
    val highestPrice: String,
    val imageUrl: String,
    val liked: Boolean,
    val priceHistory: List<PriceHistoryInfo>,
    val productId: Int,
    val productName: String,
    val reviewCount: String,
    val reviewRating: String,
    val shippingType: String,
    val unitPrice: String,
    val userId: Int
) : BaseModel