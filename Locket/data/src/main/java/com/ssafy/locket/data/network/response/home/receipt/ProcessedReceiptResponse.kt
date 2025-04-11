package com.ssafy.locket.data.network.response.home.receipt

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.home.receipt.ProcessedReceiptDetailResponse.Companion.toDomainModel
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessedReceiptResponse(
    val storeName: String,
    val items: List<ProcessedReceiptDetailResponse>,
    val totalAmount: Int,
    val categoryAmount: Map<String, Double>
): BaseResponse {
    companion object: DataMapper<ProcessedReceiptResponse, ProcessedReceipt> {
        override fun ProcessedReceiptResponse.toDomainModel(): ProcessedReceipt {
            return ProcessedReceipt(
                storeName = this.storeName,
                items = items.map { it.toDomainModel() }?: emptyList(),
                totalAmount = totalAmount,
                categoryAmount = categoryAmount
            )
        }
    }
}
