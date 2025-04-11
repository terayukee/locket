package com.ssafy.locket.repository.home.receipt

import android.net.Uri
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.model.home.receipt.ReceiptList
import kotlinx.coroutines.flow.Flow

interface ReceiptFileRepository {
    suspend fun getAvailableReceiptList(): Flow<ResponseStatus<ReceiptList>>
    suspend fun processReceiptImage(
        uri: Uri,
        transactionId: String
    ): Flow<ResponseStatus<ProcessedReceipt>>

    suspend fun processReceiptPdfFile(
        uri: Uri,
        transactionId: String
    ): Flow<ResponseStatus<ProcessedReceipt>>

    suspend fun saveReceipt(
        transactionId: String, storeName: String, items: List<ReceiptDetail>,
        totalAmount: Int,
        categoryAmount: Map<String, Double>
    ): Flow<ResponseStatus<Unit>>
}