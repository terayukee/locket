package com.ssafy.locket.usecase.budget

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.SetBudget
import com.ssafy.locket.model.finance.budget.status.BudgetStatus
import com.ssafy.locket.repository.finance.budget.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBudgetStatusUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
){
    suspend operator fun invoke(year: Int,month: Int): Flow<ResponseStatus<BudgetStatus>> {
        return budgetRepository.getBudgetStatus(year,month)
    }
}
