package com.ssafy.locket.model.graph

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val currentPrice: String,
    val discountRate: String,
    val imageUrl: String,
    val productId: Int,
    val productName: String
): BaseModel