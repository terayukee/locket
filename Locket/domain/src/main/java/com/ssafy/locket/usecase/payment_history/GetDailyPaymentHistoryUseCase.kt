package com.ssafy.locket.usecase.payment_history

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment_history.PaymentDailyHistory
import com.ssafy.locket.repository.payment_history.PaymentHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyPaymentHistoryUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    suspend operator fun invoke(year: Int, month: Int, day: Int): Flow<ResponseStatus<PaymentDailyHistory>> {
        return paymentHistoryRepository.getPaymentDailyHistory(year, month, day)
    }
}