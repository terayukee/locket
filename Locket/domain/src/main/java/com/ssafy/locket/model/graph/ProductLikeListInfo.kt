package com.ssafy.locket.model.graph

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductLikeListInfo(
    val products: List<Product>,
) : BaseModel