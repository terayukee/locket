package com.ssafy.locket.data.network.response.home.receipt

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.home.receipt.AvailableReceiptResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.receipt.AvailableReceiptResponseItem.Companion.toDomainModel
import com.ssafy.locket.model.home.receipt.ReceiptList
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailableReceiptResponse(
    @SerializedName("receipts")
    val receipts: List<AvailableReceiptResponseItem>
): BaseResponse {
    companion object: DataMapper<AvailableReceiptResponse, ReceiptList> {
        override fun AvailableReceiptResponse.toDomainModel(): ReceiptList {
            return ReceiptList(
                receiptList = this.receipts.map { it.toDomainModel() }?: emptyList()
            )
        }
    }
}