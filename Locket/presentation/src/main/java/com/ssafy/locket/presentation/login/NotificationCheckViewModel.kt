package com.ssafy.locket.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationCheckViewModel @Inject constructor(

): ViewModel() {
    private val _isIconClicked = MutableSharedFlow<Boolean>()
    val isIconClicked = _isIconClicked.asSharedFlow()

    fun setIconClicked() {
        viewModelScope.launch(Dispatchers.IO) {
            _isIconClicked.emit(true)
        }
    }
}