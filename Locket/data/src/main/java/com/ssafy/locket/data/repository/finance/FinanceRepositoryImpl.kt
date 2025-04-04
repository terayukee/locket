package com.ssafy.locket.data.repository.finance

import com.ssafy.locket.data.network.api.PaymentService
import com.ssafy.locket.repository.finance.FinanceRepository
import javax.inject.Inject

internal class FinanceRepositoryImpl @Inject constructor(
    private val paymentService: PaymentService
): FinanceRepository {

}