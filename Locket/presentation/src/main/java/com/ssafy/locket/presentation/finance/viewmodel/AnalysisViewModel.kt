package com.ssafy.locket.presentation.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.SetBudget
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.usecase.analysis.GetFeedbackUseCase
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

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val getFeedbackUseCase: GetFeedbackUseCase
): ViewModel(){
    private val _getFeedback = MutableStateFlow<GetFeedbackState>(GetFeedbackState.Initial)
    val getFeedback: StateFlow<GetFeedbackState> = _getFeedback

    fun getFeedback(year: Int,month: Int){
        viewModelScope.launch {
            getFeedbackUseCase(year,month)
                .onStart {  }
                .catch { e ->
                    Log.d("ExpenseAnalysisFragment",e.message.toString())
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

sealed class GetFeedbackState{
    object Initial: GetFeedbackState()
    object Loading: GetFeedbackState()
    data class Success(val feedback: Feedback): GetFeedbackState()
    data class Error(val message: String): GetFeedbackState()
}


