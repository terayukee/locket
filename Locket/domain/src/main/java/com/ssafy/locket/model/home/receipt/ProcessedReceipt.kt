package com.ssafy.locket.model.home.receipt

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessedReceipt(
    val storeName: String,
    val items: List<ReceiptDetail>,
    val totalAmount: Int,
    val categoryAmount: Map<String, Double>
): BaseModel