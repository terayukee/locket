package com.ssafy.locket.model.graph

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductSearchInfo(
    val page: Int,
    val pageProductCount: Int,
    val products: List<Product>,
    val searchKeyword: String,
    val searchProductCount: Int,
    val totalPages: Int
): BaseModel