package com.ssafy.locket.repository.payment_history

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment_history.PaymentCalendar
import com.ssafy.locket.model.payment_history.PaymentDailyHistory
import com.ssafy.locket.model.payment_history.PaymentMonthlyHistory
import kotlinx.coroutines.flow.Flow

interface PaymentHistoryRepository {
    suspend fun getPaymentMonthlyCalendar(year: Int, month: Int): Flow<ResponseStatus<PaymentCalendar>>
    suspend fun getPaymentMonthlyHistory(year: Int, month: Int): Flow<ResponseStatus<PaymentMonthlyHistory>>
    suspend fun getPaymentDailyHistory(year: Int, month: Int, day: Int): Flow<ResponseStatus<PaymentDailyHistory>>
}