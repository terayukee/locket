package com.ssafy.locket.presentation.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.SetBudget
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.model.finance.budget.status.BudgetStatus
import com.ssafy.locket.model.payment_history.PaymentCalendar
import com.ssafy.locket.usecase.budget.GetBudgetStatusUseCase
import com.ssafy.locket.usecase.budget.SetBugetGoalUseCase
import com.ssafy.locket.usecase.budget.getFeedbackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "BudgetViewModel"
@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val setBugetGoalUseCase: SetBugetGoalUseCase,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val getFeedbackUseCase: getFeedbackUseCase
): ViewModel() {
    private val _setBudgetGoal = MutableStateFlow<SetBudgetState>(SetBudgetState.Initial)
    val setBudgetGoal: StateFlow<SetBudgetState> = _setBudgetGoal

    private val _getBudgetStatus = MutableStateFlow<GetBudgetStatusState>(GetBudgetStatusState.Initial)
    val getBudgetStatus: StateFlow<GetBudgetStatusState> = _getBudgetStatus

    private val _getFeedback = MutableStateFlow<GetFeedbackState>(GetFeedbackState.Initial)
    val getFeedback: StateFlow<GetFeedbackState> = _getFeedback

    fun setBudgetGoal(amount: Int){
        viewModelScope.launch {
            setBugetGoalUseCase(amount)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthlyPaymentHistory: Error ${e.message}")
                }
                .collect{ status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _setBudgetGoal.value = SetBudgetState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _setBudgetGoal.value = SetBudgetState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun getBudgetStatus(year: Int, month: Int){
        viewModelScope.launch {
            getBudgetStatusUseCase(year,month)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthlyPaymentHistory: Error ${e.message}")
                }
                .collect{ status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _getBudgetStatus.value = GetBudgetStatusState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _getBudgetStatus.value = GetBudgetStatusState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun getFeedback(year: Int, month: Int){
        viewModelScope.launch {
            getFeedbackUseCase(year,month)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthlyPaymentHistory: Error ${e.message}")
                }
                .collect{ status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _getFeedback.value = GetFeedbackState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _getFeedback.value = GetFeedbackState.Error(status.error.message)
                        }
                    }
                }
        }
    }



}

sealed class SetBudgetState{
    object Initial: SetBudgetState()
    object Loading: SetBudgetState()
    data class Success(val setBudget: SetBudget): SetBudgetState()
    data class Error(val message: String): SetBudgetState()
}
sealed class GetBudgetStatusState{
    object Initial: GetBudgetStatusState()
    object Loading: GetBudgetStatusState()
    data class Success(val budgetStatus: BudgetStatus): GetBudgetStatusState()
    data class Error(val message: String): GetBudgetStatusState()
}
sealed class GetFeedbackState{
    object Initial: GetFeedbackState()
    object Loading: GetFeedbackState()
    data class Success(val feedback: Feedback): GetFeedbackState()
    data class Error(val message: String): GetFeedbackState()
}