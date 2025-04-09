package com.ssafy.locket.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.status.BudgetStatus
import com.ssafy.locket.presentation.finance.viewmodel.GetShortFeedbackState
import com.ssafy.locket.presentation.finance.viewmodel.TotalPaymentState
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.usecase.finance.budget.GetBudgetStatusUseCase
import com.ssafy.locket.usecase.finance.budget.getShortFeedbackUseCase
import com.ssafy.locket.usecase.finance.payment_history.GetPaymentMonthlyTotalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject

private const val TAG = "HomeFinanceViewModel"
@HiltViewModel
class HomeFinanceViewModel @Inject constructor(
    private val getPaymentMonthlyTotalUseCase: GetPaymentMonthlyTotalUseCase,
    private val getBudgetStatusUseCase: GetBudgetStatusUseCase,
    private val getShortFeedbackUseCase: getShortFeedbackUseCase
): ViewModel(){

    private val _monthTotal = MutableStateFlow<TotalPaymentState>(TotalPaymentState.Initial)
    val monthTotal: StateFlow<TotalPaymentState> = _monthTotal.asStateFlow()

    private val _prevMonthTotal = MutableStateFlow<TotalPaymentState>(TotalPaymentState.Initial)
    val prevMonthTotal: StateFlow<TotalPaymentState> = _prevMonthTotal.asStateFlow()

    private val _monthBudget = MutableStateFlow<MonthlyBudgetState>(MonthlyBudgetState.Initial)
    val monthBudget: StateFlow<MonthlyBudgetState> = _monthBudget.asStateFlow()

    private val _shortFeedback = MutableStateFlow<GetShortFeedbackState>(GetShortFeedbackState.Initial)
    val shortFeedback: StateFlow<GetShortFeedbackState> = _shortFeedback

    private val currentYearMonth: YearMonth = YearMonth.now()

    fun getMonthTotal() {
        viewModelScope.launch {
            getPaymentMonthlyTotalUseCase(currentYearMonth.year, currentYearMonth.monthValue)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthTotal: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _monthTotal.value = TotalPaymentState.Success(status.data.totalAmount)
                            getPrevMonthTotal(currentYearMonth.minusMonths(1), status.data.totalAmount)
                        }
                        is ResponseStatus.Error -> {
                            _monthTotal.value = TotalPaymentState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    private fun getPrevMonthTotal(yearMonth: YearMonth, monthTotal: BigDecimal){
        viewModelScope.launch {
            getPaymentMonthlyTotalUseCase(yearMonth.year, yearMonth.monthValue)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getPrevMonthTotal: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            val difference = CommonUtils.floorTenThousandDecimal(status.data.totalAmount, monthTotal)
                            _prevMonthTotal.value = TotalPaymentState.Success(difference)
                        }
                        is ResponseStatus.Error -> {
                            _prevMonthTotal.value = TotalPaymentState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun getMonthlyBudget() {
        viewModelScope.launch {
            getBudgetStatusUseCase(currentYearMonth.year, currentYearMonth.monthValue)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthlyBudget: ${e.message}")
                    _monthBudget.value = MonthlyBudgetState.Error(e.message ?: "알 수 없는 오류가 발생했습니다")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG, "getMonthlyBudget: ${status.data}")
                            _monthBudget.value = MonthlyBudgetState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG, "getMonthlyBudget: ${status.error.message}")
                            _monthBudget.value = MonthlyBudgetState.Error(status.error.message)
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
                            _shortFeedback.value = GetShortFeedbackState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _shortFeedback.value = GetShortFeedbackState.Error(status.error.message)
                        }
                    }
                }
        }
    }
}

sealed class MonthlyBudgetState {
    object Initial: MonthlyBudgetState()
    data class Success(val budgetStatus: BudgetStatus): MonthlyBudgetState()
    data class Error(val message: String): MonthlyBudgetState()
}
