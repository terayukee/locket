package com.ssafy.locket.presentation.finance.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment_history.PaymentDailyHistory
import com.ssafy.locket.usecase.payment_history.GetDailyPaymentHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "SelectedDayViewModel"
@HiltViewModel
class SelectedDayViewModel @Inject constructor(
    private val getDailyPaymentHistoryUseCase: GetDailyPaymentHistoryUseCase
): ViewModel() {
    private val _selectedDay = MutableStateFlow<SelectedDayState>(SelectedDayState.Initial)
    val selectedDay = _selectedDay.asStateFlow()

    private val _selectedDayPayments = MutableStateFlow<SelectedDayPaymentsState>(SelectedDayPaymentsState.Initial)
    val selectedDayPayments = _selectedDayPayments.asStateFlow()

    private val _openDialog = Channel<OpenDialogState>(Channel.BUFFERED)
    val openDialog = _openDialog.receiveAsFlow()

    fun setSelectedDay(day: LocalDate) {
        _selectedDay.value = SelectedDayState.Selected(day)
    }

    fun clearSelectedDay() {
        _selectedDay.value = SelectedDayState.Initial
    }

    fun setSelectedDayPayments(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            getDailyPaymentHistoryUseCase(year, month, day)
                .onStart {  }
                .catch {e ->
                    _selectedDayPayments.value = SelectedDayPaymentsState.Error(e.message ?: "Unknown Error")
                    Log.e(TAG, "시스템 레벨 예외: ${e.stackTraceToString()}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _selectedDayPayments.value = SelectedDayPaymentsState.Success(status.data)
                            if(status.data.list.isNotEmpty()) {
                                Log.d(TAG, "setSelectedDayPayments: dialog is added")
                                _openDialog.trySend(OpenDialogState.Opened)
                            }
                        }
                        is ResponseStatus.Error -> {
                            _selectedDayPayments.value = SelectedDayPaymentsState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun clearSelectedDayPayments() {
        _selectedDayPayments.value = SelectedDayPaymentsState.Initial
    }

}

sealed class SelectedDayState {
    object Initial: SelectedDayState()
    data class Selected(val day: LocalDate): SelectedDayState()
}

sealed class SelectedDayPaymentsState {
    object Initial: SelectedDayPaymentsState()
    data class Success(val paymentDailyHistory: PaymentDailyHistory): SelectedDayPaymentsState()
    data class Error(val message: String): SelectedDayPaymentsState()
}

sealed class OpenDialogState {
    object Initial: OpenDialogState()
    object Opened: OpenDialogState()
    object Error: OpenDialogState()
}