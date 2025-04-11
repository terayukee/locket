package com.ssafy.locket.repository.payment

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment.CheckFingerprint
import com.ssafy.locket.model.payment.PasswordVerify
import com.ssafy.locket.model.payment.Payment
import com.ssafy.locket.model.payment.PaymentCardList
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface PaymentRepository {
    suspend fun pay(
        paymentKey: String,
        cardId: Int,
        sellerId: Long,
        paymentCategory: String,
        paymentMerchant: String,
        amount: BigDecimal,
        storeName: String
    ): Flow<ResponseStatus<Payment>>

    suspend fun verifyPassword(password: Int): Flow<ResponseStatus<PasswordVerify>>
    suspend fun getCards(): Flow<ResponseStatus<PaymentCardList>>
    suspend fun checkFingerprintRegistered(): Flow<ResponseStatus<CheckFingerprint>>

}