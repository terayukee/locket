package com.ssafy.locket.usecase.home.receipt

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.repository.home.receipt.ReceiptFileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveReceiptUseCase @Inject constructor(
    private val receiptFileRepository: ReceiptFileRepository
) {
    suspend operator fun invoke(transactionId: String, storeName: String, items: List<ReceiptDetail>, totalAmount: Int, categoryAmount: Map<String, Double>): Flow<ResponseStatus<Unit>> {
        return receiptFileRepository.saveReceipt(transactionId, storeName, items, totalAmount, categoryAmount)
    }
}