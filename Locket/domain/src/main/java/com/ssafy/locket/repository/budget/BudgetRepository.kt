package com.ssafy.locket.repository.budget

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.SetBudget
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.model.finance.budget.status.BudgetStatus
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun setBudgetGoal(amount: Int) : Flow<ResponseStatus<SetBudget>>
    suspend fun getBudgetStatus(year: Int,month: Int) : Flow<ResponseStatus<BudgetStatus>>
    suspend fun getFeedback(year:Int,month:Int) : Flow<ResponseStatus<Feedback>>
}