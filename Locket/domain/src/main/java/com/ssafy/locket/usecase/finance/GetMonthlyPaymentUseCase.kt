package com.ssafy.locket.usecase.finance

import com.ssafy.locket.repository.finance.FinanceRepository
import javax.inject.Inject

class GetMonthlyPaymentUseCase @Inject constructor(
    private val financeRepository: FinanceRepository
){
//    suspend fun execute(page: Int): List<> {
//        return financeRepository.getPaymentList()
//    }
    // TODO 추후 변경 예정
}