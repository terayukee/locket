package com.ssafy.locket.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.presentation.finance.viewmodel.TotalPaymentState
import com.ssafy.locket.presentation.utils.CommonUtils
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
    private val getPaymentMonthlyTotalUseCase: GetPaymentMonthlyTotalUseCase
): ViewModel(){

    private val _monthTotal = MutableStateFlow<TotalPaymentState>(TotalPaymentState.Initial)
    val monthTotal: StateFlow<TotalPaymentState> = _monthTotal.asStateFlow()

    private val _prevMonthTotal = MutableStateFlow<TotalPaymentState>(TotalPaymentState.Initial)
    val prevMonthTotal: StateFlow<TotalPaymentState> = _prevMonthTotal.asStateFlow()

    fun getMonthTotal(yearMonth: YearMonth){
        viewModelScope.launch {
            getPaymentMonthlyTotalUseCase(yearMonth.year, yearMonth.monthValue)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthTotal: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _monthTotal.value = TotalPaymentState.Success(status.data.totalAmount)
                            getPrevMonthTotal(yearMonth.minusMonths(1), status.data.totalAmount)
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
}
