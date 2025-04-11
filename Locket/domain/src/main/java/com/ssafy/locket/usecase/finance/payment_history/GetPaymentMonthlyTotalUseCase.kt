package com.ssafy.locket.usecase.finance.payment_history

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.payment_history.PaymentMonthlyTotal
import com.ssafy.locket.repository.finance.payment_history.PaymentHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPaymentMonthlyTotalUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
){
    suspend operator fun invoke(year: Int, month: Int): Flow<ResponseStatus<PaymentMonthlyTotal>> {
        return paymentHistoryRepository.getPaymentMonthlyTotal(year, month)
    }
}