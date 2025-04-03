package com.ssafy.locket.data.repository.payment

import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.PaymentService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.request.payment.CardValidationRequest
import com.ssafy.locket.data.network.request.payment.PassswordVerifyRequest
import com.ssafy.locket.data.network.request.payment.PaymentRequest
import com.ssafy.locket.data.network.response.payment.CardListResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.payment.CheckFingerprintResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.payment.PasswordVerifyResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.payment.PaymentResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment.CheckFingerprint
import com.ssafy.locket.model.payment.PasswordVerify
import com.ssafy.locket.model.payment.Payment
import com.ssafy.locket.model.payment.PaymentCardList
import com.ssafy.locket.repository.payment.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.math.BigDecimal
import javax.inject.Inject

internal class PaymentRepositoryImpl @Inject constructor(
    private val paymentService: PaymentService,
    private val dataStore: UserDataStoreSource
): PaymentRepository {
    override suspend fun pay(
        paymentKey: String,
        cardId: Int,
        sellerId: Long,
        paymentCategory: String,
        paymentMerchant: String,
        amount: BigDecimal,
        storeName: String
    ): Flow<ResponseStatus<Payment>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                paymentService.pay(userId, PaymentRequest(amount, cardId, paymentCategory, paymentKey, paymentMerchant, sellerId, storeName))
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

    override suspend fun verifyPassword(password: Int): Flow<ResponseStatus<PasswordVerify>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                paymentService.verifyPassword(userId, PassswordVerifyRequest(password))
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

    override suspend fun getCards(): Flow<ResponseStatus<PaymentCardList>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                paymentService.getCards(userId)
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

    override suspend fun checkFingerprintRegistered(): Flow<ResponseStatus<CheckFingerprint>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                paymentService.checkFingerPrintRegistered(userId)
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