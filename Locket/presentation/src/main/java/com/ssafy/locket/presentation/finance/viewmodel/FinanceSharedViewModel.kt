package com.ssafy.locket.presentation.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.usecase.finance.payment_history.GetPaymentMonthlyTotalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.YearMonth
import javax.inject.Inject

private const val TAG = "FinanceSharedViewModel"
@HiltViewModel
class FinanceSharedViewModel @Inject constructor(
    private val getPaymentMonthlyTotalUseCase: GetPaymentMonthlyTotalUseCase
): ViewModel() {
    private var totalPaymentJob: Job? = null

    private val _selectedYearMonth = MutableStateFlow(YearMonth.now())
    val selectedYearMonth: StateFlow<YearMonth> = _selectedYearMonth.asStateFlow()

    private val _selectedYearMonthTotalPayment =
        MutableStateFlow<TotalPaymentState>(TotalPaymentState.Initial)
    val selectedYearMonthTotalPayment: StateFlow<TotalPaymentState> =
        _selectedYearMonthTotalPayment.asStateFlow()

    fun setYearMonth(newYearMonth: YearMonth) {
        viewModelScope.launch {
            _selectedYearMonth.update { newYearMonth }
            Log.d(TAG, "setYearMonth: newYearMonth ${newYearMonth.year} ${newYearMonth.monthValue}")
            getTotalPayment(newYearMonth.year, newYearMonth.monthValue)
        }
    }

    fun initYearMonth() {
        _selectedYearMonth.value = YearMonth.now()
    }
    fun initYearMonthPayment() {
        viewModelScope.launch {
//            _selectedYearMonth.update { _selectedYearMonth.value }
//            Log.d(TAG, "initYearMonth: ${_selectedYearMonth.value.year}  ${_selectedYearMonth.value.monthValue}")
            getTotalPayment(_selectedYearMonth.value.year, _selectedYearMonth.value.monthValue)
        }
    }

    fun getTotalPayment(year: Int, month: Int) {
        totalPaymentJob?.cancel()

        totalPaymentJob = viewModelScope.launch {
            getPaymentMonthlyTotalUseCase(year, month)
                .onStart { }
                .catch { e ->
                    Log.d(TAG, "getTotalPayment: ${e.message}")
                }
                .collect { status ->
                    when (status) {
                        is ResponseStatus.Success -> {
                            _selectedYearMonthTotalPayment.value =
                                TotalPaymentState.Success(status.data.totalAmount)
                        }

                        is ResponseStatus.Error -> {
                            _selectedYearMonthTotalPayment.value =
                                TotalPaymentState.Error(status.error.message)
                        }
                    }
                }

        }
    }
}

sealed class TotalPaymentState{
    object Initial: TotalPaymentState()
    data class Success(val totalPayment: BigDecimal): TotalPaymentState()
    data class Error(val message: String): TotalPaymentState()
}
