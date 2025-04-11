package com.ssafy.locket.data.network.response.home.receipt

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessedReceiptDetailResponse (
    val itemId: Long,
    val itemAmount: Int,
    val itemCategory: String,
    val itemName: String,
    val itemQuantity: Int
): BaseResponse {
    companion object: DataMapper<ProcessedReceiptDetailResponse, ReceiptDetail> {
        override fun ProcessedReceiptDetailResponse.toDomainModel(): ReceiptDetail {
            return ReceiptDetail(
                itemId = this.itemId,
                itemAmount = this.itemAmount,
                itemCategory = this.itemCategory,
                itemName = this.itemName,
                itemQuantity = this.itemQuantity
            )
        }
    }
}