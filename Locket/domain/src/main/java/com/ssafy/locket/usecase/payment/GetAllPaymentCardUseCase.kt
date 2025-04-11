package com.ssafy.locket.usecase.payment

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.GifticonList
import com.ssafy.locket.model.payment.PaymentCardList
import com.ssafy.locket.repository.payment.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPaymentCardUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    suspend operator fun invoke(): Flow<ResponseStatus<PaymentCardList>> {
        return paymentRepository.getCards()
    }
}