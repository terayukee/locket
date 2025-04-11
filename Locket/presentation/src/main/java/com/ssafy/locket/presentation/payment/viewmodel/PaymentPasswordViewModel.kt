package com.ssafy.locket.presentation.payment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.usecase.payment.CheckPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PaymentPasswordViewMode"
@HiltViewModel
class PaymentPasswordViewModel @Inject constructor(
    private val checkPasswordUseCase: CheckPasswordUseCase
): ViewModel() {
    private var _isPasswordVerify = MutableSharedFlow<Boolean>()
    val isPasswordVerify: Flow<Boolean> = _isPasswordVerify.asSharedFlow()

    fun checkPassword(password: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            checkPasswordUseCase(password)
                .onStart {  }
                .catch { e ->
                    _isPasswordVerify.emit(false)
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
//                            Log.d(TAG, "checkPassword: Success ${status.data}")
                            _isPasswordVerify.emit(status.data.valid)
                        }
                        is ResponseStatus.Error -> {
//                            Log.d(TAG, "checkPassword: Error ${status.error.message}")
                            _isPasswordVerify.emit(false)
                        }
                    }
                }
        }
    }
}