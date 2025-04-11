package com.ssafy.locket.usecase.finance.payment_history

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.GifticonList
import com.ssafy.locket.model.finance.payment_history.PaymentMonthlyHistory
import com.ssafy.locket.repository.finance.payment_history.PaymentHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyPaymentHistoryUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
){
    suspend operator fun invoke(year: Int, month: Int): Flow<ResponseStatus<PaymentMonthlyHistory>> {
        return paymentHistoryRepository.getPaymentMonthlyHistory(year, month)
    }
}