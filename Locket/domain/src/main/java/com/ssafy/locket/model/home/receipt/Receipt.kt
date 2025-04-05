package com.ssafy.locket.model.home.receipt

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Receipt(
    val amount: Int,
    val cardName: String,
    val category: String,
    val paymentDate: String,
    val storeName: String,
    val transactionId: String
): BaseModel