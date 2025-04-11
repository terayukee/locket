package com.ssafy.locket.model.home.receipt

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceiptList(
    @SerializedName("receipts") val receiptList: List<Receipt>
): BaseModel