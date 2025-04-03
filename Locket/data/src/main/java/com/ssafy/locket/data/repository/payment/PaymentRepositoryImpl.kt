package com.ssafy.locket.data.repository.payment

import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.PaymentService
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.repository.payment.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class PaymentRepositoryImpl @Inject constructor(
    private val paymentService: PaymentService,
    private val dataStore: UserDataStoreSource
): PaymentRepository {
    override suspend fun validateCard(): Flow<ResponseStatus<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun pay(): Flow<ResponseStatus<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun verifyPassword(): Flow<ResponseStatus<Unit>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCards(): Flow<ResponseStatus<Unit>> {
        TODO("Not yet implemented")
    }

}