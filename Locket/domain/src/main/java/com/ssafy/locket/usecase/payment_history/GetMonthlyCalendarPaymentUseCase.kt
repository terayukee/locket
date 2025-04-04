package com.ssafy.locket.usecase.payment_history

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment_history.PaymentCalendar
import com.ssafy.locket.repository.payment_history.PaymentHistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyCalendarPaymentUseCase @Inject constructor(
    private val paymentHistoryRepository: PaymentHistoryRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Flow<ResponseStatus<PaymentCalendar>> {
        return paymentHistoryRepository.getPaymentMonthlyCalendar(year, month)
    }
}