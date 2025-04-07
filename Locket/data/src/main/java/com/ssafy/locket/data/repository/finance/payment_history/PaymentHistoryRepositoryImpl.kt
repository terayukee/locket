package com.ssafy.locket.data.repository.finance.payment_history

import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.PaymentHistoryService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentDailyHistoryResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyCalendarResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyHistoryResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyTotalResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment_history.PaymentCalendar
import com.ssafy.locket.model.payment_history.PaymentDailyHistory
import com.ssafy.locket.model.payment_history.PaymentMonthlyHistory
import com.ssafy.locket.model.payment_history.PaymentMonthlyTotal
import com.ssafy.locket.repository.finance.payment_history.PaymentHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class PaymentHistoryRepositoryImpl @Inject constructor(
    private val paymentHistoryService: PaymentHistoryService,
    private val dataStore: UserDataStoreSource
): PaymentHistoryRepository {
    override suspend fun getPaymentMonthlyCalendar(
        year: Int,
        month: Int
    ): Flow<ResponseStatus<PaymentCalendar>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                paymentHistoryService.getPaymentCalendar(userId, year, month)
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun getPaymentMonthlyHistory(
        year: Int,
        month: Int
    ): Flow<ResponseStatus<PaymentMonthlyHistory>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                paymentHistoryService.getPaymentMonthlyHistory(userId, year, month)
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun getPaymentDailyHistory(
        year: Int,
        month: Int,
        day: Int
    ): Flow<ResponseStatus<PaymentDailyHistory>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                paymentHistoryService.getPaymentDailyHistory(userId, year, month, day)
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun getPaymentMonthlyTotal(
        year: Int,
        month: Int
    ): Flow<ResponseStatus<PaymentMonthlyTotal>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                paymentHistoryService.getPaymentMonthlyTotal(userId, year, month)
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }
}