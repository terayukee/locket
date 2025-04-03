package com.ssafy.locket.repository.payment

import com.ssafy.locket.model.base.ResponseStatus
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun validateCard(): Flow<ResponseStatus<Unit>>
    suspend fun pay(): Flow<ResponseStatus<Unit>>
    suspend fun verifyPassword(): Flow<ResponseStatus<Unit>>
    suspend fun getCards(): Flow<ResponseStatus<Unit>>

}