package com.ssafy.locket.presentation.finance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class FinanceSharedViewModel @Inject constructor(
//    private val getMonthlyPaymentUseCase: GetMonthlyPaymentUseCase
): ViewModel() {
    private val _selectedYearMonth = MutableStateFlow(YearMonth.now())
    val selectedYearMonth: StateFlow<YearMonth> = _selectedYearMonth.asStateFlow()

    fun setYearMonth(newYearMonth: YearMonth) {
        viewModelScope.launch {
            _selectedYearMonth.update { newYearMonth }
            // TODO api 연결하여 로직 추가 예정
        }
    }

    fun initYearMonth() {
        viewModelScope.launch {
            _selectedYearMonth.update { YearMonth.now() }
        }
    }
}
