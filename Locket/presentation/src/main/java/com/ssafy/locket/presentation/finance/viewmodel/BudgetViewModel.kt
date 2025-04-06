package com.ssafy.locket.presentation.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.SetBudget
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.model.finance.budget.feedback.ShortFeedback
import com.ssafy.locket.model.finance.budget.status.BudgetStatus
import com.ssafy.locket.usecase.budget.GetBudgetStatusUseCase
import com.ssafy.locket.usecase.budget.SetBugetGoalUseCase
import com.ssafy.locket.usecase.budget.getShortFeedbackUseCase
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
    private val getShortFeedbackUseCase: getShortFeedbackUseCase
): ViewModel() {
    private val _setBudgetGoal = MutableStateFlow<SetBudgetState>(SetBudgetState.Initial)
    val setBudgetGoal: StateFlow<SetBudgetState> = _setBudgetGoal

    private val _getBudgetStatus = MutableStateFlow<GetBudgetStatusState>(GetBudgetStatusState.Initial)
    val getBudgetStatus: StateFlow<GetBudgetStatusState> = _getBudgetStatus

    private val _getShortFeedback = MutableStateFlow<GetShortFeedbackState>(GetShortFeedbackState.Initial)
    val getShortFeedback: StateFlow<GetShortFeedbackState> = _getShortFeedback

    fun setBudgetGoal(amount: Int){
        viewModelScope.launch {
            setBugetGoalUseCase(amount)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "setBudgetGoal: Error ${e.message}")
                }
                .collect{ status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG,status.data.toString())
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
                    Log.d(TAG, "getBudgetStatus: Error ${e.message}")
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

    fun getShortFeedback(){
        viewModelScope.launch {
            getShortFeedbackUseCase()
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthlyPaymentHistory: Error ${e.message}")
                }
                .collect{ status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _getShortFeedback.value = GetShortFeedbackState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _getShortFeedback.value = GetShortFeedbackState.Error(status.error.message)
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
sealed class GetShortFeedbackState{
    object Initial: GetShortFeedbackState()
    object Loading: GetShortFeedbackState()
    data class Success(val shortFeedback: ShortFeedback): GetShortFeedbackState()
    data class Error(val message: String): GetShortFeedbackState()
}