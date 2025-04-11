package com.ssafy.locket.usecase.home.receipt

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.receipt.ReceiptList
import com.ssafy.locket.repository.home.receipt.ReceiptFileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetAllAvailableReceiptsUseCase @Inject constructor(
    private val receiptFileRepository: ReceiptFileRepository
) {
    suspend operator fun invoke() : Flow<ResponseStatus<ReceiptList>> {
        return receiptFileRepository.getAvailableReceiptList()
    }
}