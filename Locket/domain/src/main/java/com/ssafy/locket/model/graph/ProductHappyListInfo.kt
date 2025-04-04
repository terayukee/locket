package com.ssafy.locket.model.graph

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductHappyListInfo(
    val products: List<Product>,
) :BaseModel