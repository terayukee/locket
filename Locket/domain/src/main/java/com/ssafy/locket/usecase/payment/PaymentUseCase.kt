package com.ssafy.locket.usecase.payment

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment.PasswordVerify
import com.ssafy.locket.model.payment.Payment
import com.ssafy.locket.repository.payment.PaymentRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

class PaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
){
    suspend operator fun invoke(paymentKey: String,
                                cardId: Int,
                                sellerId: Long,
                                paymentCategory: String,
                                paymentMerchant: String,
                                amount: BigDecimal,
                                storeName: String): Flow<ResponseStatus<Payment>> {
        return paymentRepository.pay(paymentKey, cardId, sellerId, paymentCategory, paymentMerchant, amount, storeName)
    }
}