package com.ssafy.locket.model.graph.product_detail

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceHistoryInfo(
    val highestPrice: Int,
    val lowestPrice: Int,
    val priceDate: String
) : BaseModel