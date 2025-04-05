package com.ssafy.locket.usecase.home.receipt

import android.net.Uri
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import com.ssafy.locket.repository.home.receipt.ReceiptFileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProcessReceiptUseCase @Inject constructor(
    private val receiptFileRepository: ReceiptFileRepository
){
    suspend operator fun invoke(type: String, uri: Uri, transactionId: String): Flow<ResponseStatus<ProcessedReceipt>> {
        if("image".equals(type))
            return receiptFileRepository.processReceiptImage(uri, transactionId)
        else
            return receiptFileRepository.processReceiptPdfFile(uri, transactionId)
    }
}