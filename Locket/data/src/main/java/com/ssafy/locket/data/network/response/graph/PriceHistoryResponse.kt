package com.ssafy.locket.data.network.response.graph

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.graph.ProductResponse.Companion.toDomainModel
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import com.ssafy.locket.model.graph.product_detail.PriceHistoryInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceHistoryResponse(
    val highestPrice: Int,
    val lowestPrice: Int,
    val priceDate: String
) : BaseResponse {
    companion object : DataMapper<PriceHistoryResponse, PriceHistoryInfo> {
        override fun PriceHistoryResponse.toDomainModel(): PriceHistoryInfo {
            return PriceHistoryInfo(
                highestPrice = this.highestPrice,
                lowestPrice = this.lowestPrice,
                priceDate = this.priceDate
            )
        }
    }
}