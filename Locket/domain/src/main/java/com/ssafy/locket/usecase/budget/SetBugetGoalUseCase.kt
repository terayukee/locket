package com.ssafy.locket.usecase.budget

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.SetBudget
import com.ssafy.locket.repository.budget.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetBugetGoalUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
){
    suspend operator fun invoke(amount: Int): Flow<ResponseStatus<SetBudget>> {
        return budgetRepository.setBudgetGoal(amount)
    }
}