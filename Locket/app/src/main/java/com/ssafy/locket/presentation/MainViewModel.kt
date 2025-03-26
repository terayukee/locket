package com.ssafy.locket.presentation

import androidx.lifecycle.ViewModel
import com.ssafy.locket.data.model.dto.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(): ViewModel(){
    private val _selectedFinanceTab = MutableStateFlow<FinanceNavigationState>(FinanceNavigationState.Default)
    val selectedFinanceTab: StateFlow<FinanceNavigationState> = _selectedFinanceTab.asStateFlow()

    fun setSelectedFinanceTab(state: FinanceNavigationState){
        _selectedFinanceTab.value = state
    }

}


sealed class FinanceNavigationState {
    object Default : FinanceNavigationState()
    object Budget : FinanceNavigationState()
}