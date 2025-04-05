package com.ssafy.locket.data.network.response.home.receipt

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.receipt.Receipt
import kotlinx.parcelize.Parcelize

@Parcelize
data class AvailableReceiptResponseItem(
    @SerializedName("amount") val amount: Int,
    @SerializedName("cardName") val cardName: String,
    @SerializedName("paymentCategory") val category: String,
    @SerializedName("paymentDate") val paymentDate: String,
    @SerializedName("storeName") val storeName: String,
    @SerializedName("transactionId") val transactionId: String
) : BaseResponse {
    companion object : DataMapper<AvailableReceiptResponseItem, Receipt> {
        override fun AvailableReceiptResponseItem.toDomainModel(): Receipt {
            return Receipt(
                amount = this.amount,
                cardName = this.cardName,
                category = this.category,
                paymentDate = this.paymentDate,
                storeName = this.storeName,
                transactionId = this.transactionId
            )
        }
    }
}