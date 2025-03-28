package com.ssafy.locket.ui.payment.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RecertifyViewModel : ViewModel() {
    private val _recertify = MutableStateFlow(0)
    val recertify: StateFlow<Int> get() = _recertify
    fun updateRecertify(newRecertify: Int) {
        _recertify.value = newRecertify
    }
}