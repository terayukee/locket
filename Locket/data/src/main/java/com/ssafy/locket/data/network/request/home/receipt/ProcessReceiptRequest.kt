package com.ssafy.locket.data.network.request.home.receipt

import com.ssafy.locket.data.network.response.home.receipt.ProcessedReceiptDetailResponse

data class ProcessReceiptRequest(
    val transactionId: String,
    val userId: Long,
    val items: List<ProcessReceiptDetailRequest>,
    val totalAmount: Int,
    val categoryAmount: Map<String, Double>
)
