package com.ssafy.locket.usecase.finance.budget

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.model.finance.budget.feedback.ShortFeedback
import com.ssafy.locket.repository.finance.budget.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class getShortFeedbackUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
){
    suspend operator fun invoke(): Flow<ResponseStatus<ShortFeedback>> {
        return budgetRepository.getShortFeedback()
    }
}