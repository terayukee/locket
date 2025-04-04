package com.ssafy.locket.presentation.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment_history.PaymentCalendar
import com.ssafy.locket.model.payment_history.PaymentDailyHistory
import com.ssafy.locket.model.payment_history.PaymentMonthlyHistory
import com.ssafy.locket.usecase.payment_history.GetDailyPaymentHistoryUseCase
import com.ssafy.locket.usecase.payment_history.GetMonthlyCalendarPaymentUseCase
import com.ssafy.locket.usecase.payment_history.GetMonthlyPaymentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

private const val TAG = "PaymentHistoryViewModel"
@HiltViewModel
class PaymentHistoryViewModel @Inject constructor(
    private val getMonthlyPaymentHistoryUseCase: GetMonthlyPaymentHistoryUseCase,
    private val getMonthlyCalendarPaymentUseCase: GetMonthlyCalendarPaymentUseCase,
    private val getDailyPaymentHistoryUseCase: GetDailyPaymentHistoryUseCase
): ViewModel(){
    private val _monthlyPaymentHistory = MutableStateFlow<PaymentHistoryState>(PaymentHistoryState.Initial)
    val monthlyPaymentHistory: StateFlow<PaymentHistoryState> = _monthlyPaymentHistory

    private val _monthlyPaymentCalendar = MutableStateFlow<PaymentCalendarState>(PaymentCalendarState.Initial)
    val monthlyPaymentCalendar: StateFlow<PaymentCalendarState> = _monthlyPaymentCalendar

    private val _dailyPaymentHistory = MutableStateFlow<DailyPaymentState>(DailyPaymentState.Initial)
    val dailyPaymentHistory: StateFlow<DailyPaymentState> = _dailyPaymentHistory

    fun getMonthlyPaymentHistory(year: Int, month: Int){
        viewModelScope.launch {
            getMonthlyPaymentHistoryUseCase(year, month)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthlyPaymentHistory: Error ${e.message}")
                }
                .collect{ status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _monthlyPaymentHistory.value = PaymentHistoryState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _monthlyPaymentHistory.value = PaymentHistoryState.Error(status.error.message)
                        }
                    }
                }
            }
    }

    fun getMonthlyPaymentCalendar(year: Int, month: Int){
        viewModelScope.launch {
            getMonthlyCalendarPaymentUseCase(year, month)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthlyPaymentCalendar: Error ${e.message}")
                }
                .collect{ status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _monthlyPaymentCalendar.value = PaymentCalendarState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _monthlyPaymentCalendar.value = PaymentCalendarState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun getDailyPaymentHistory(year: Int, month: Int, day: Int){
        viewModelScope.launch {
            getDailyPaymentHistoryUseCase(year, month, day)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getMonthlyPaymentCalendar: Error ${e.message}")
                }
                .collect{ status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _dailyPaymentHistory.value = DailyPaymentState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _dailyPaymentHistory.value = DailyPaymentState.Error(status.error.message)
                        }
                    }
                }
        }
    }
}

sealed class PaymentHistoryState{
    object Initial: PaymentHistoryState()
    object Loading: PaymentHistoryState()
    data class Success(val paymentMonthlyHistory: PaymentMonthlyHistory): PaymentHistoryState()
    data class Error(val message: String): PaymentHistoryState()
}

sealed class PaymentCalendarState{
    object Initial: PaymentCalendarState()
    object Loading: PaymentCalendarState()
    data class Success(val paymentCalendar: PaymentCalendar): PaymentCalendarState()
    data class Error(val message: String): PaymentCalendarState()
}

sealed class DailyPaymentState{
    object Initial: DailyPaymentState()
    object Loading: DailyPaymentState()
    data class Success(val paymentDailyHistory: PaymentDailyHistory): DailyPaymentState()
    data class Error(val message: String): DailyPaymentState()
}