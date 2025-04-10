package com.ssafy.locket.presentation.payment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment.PaymentCardList
import com.ssafy.locket.usecase.payment.CheckFingerprintRegisteredUseCase
import com.ssafy.locket.usecase.payment.GetAllPaymentCardUseCase
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

private const val TAG = "CardPaymentViewModel"
@HiltViewModel
class CardPaymentViewModel @Inject constructor(
    private val getAllPaymentCardUseCase: GetAllPaymentCardUseCase,
    private val checkFingerprintRegisteredUseCase: CheckFingerprintRegisteredUseCase,
): ViewModel() {
    private var _paymentCardList = MutableStateFlow<PaymentCardState>(PaymentCardState.Initial)
    val paymentCardList: Flow<PaymentCardState> = _paymentCardList.asStateFlow()

    private var _isFingerprintRegistered = MutableSharedFlow<Boolean>()
    val isFingerprintRegistered: Flow<Boolean> = _isFingerprintRegistered.asSharedFlow()

    fun getAllPaymentCards() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllPaymentCardUseCase()
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getAllPaymentCards: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
//                            Log.d(TAG, "getAllPaymentCards: Success ${status.data}")
                            _paymentCardList.value = PaymentCardState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
//                            Log.d(TAG, "getAllPaymentCards: Error ${status.error.message}")
                            _paymentCardList.value = PaymentCardState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun checkFingerprintRegistered() {
        viewModelScope.launch(Dispatchers.IO) {
            checkFingerprintRegisteredUseCase()
                .onStart { }
                .catch { e ->
                    Log.d(TAG, "checkFingerprintRegistered: ${e.message}")
                }
                .collect { status ->
                    when (status) {
                        is ResponseStatus.Success -> {
//                            Log.d(TAG, "checkFingerprintRegistered: Success ${status.data}")
                            _isFingerprintRegistered.emit(status.data.fingerprintRegistered)
                        }

                        is ResponseStatus.Error -> {
//                            Log.d(TAG, "checkFingerprintRegistered: Error ${status.error.message}")
                            _isFingerprintRegistered.emit(false)
                        }
                    }
                }
        }
    }
}

sealed class PaymentCardState {
    object Initial: PaymentCardState()
    object Loading: PaymentCardState()
    data class Success(val paymentCardList: PaymentCardList): PaymentCardState()
    data class Error(val message: String): PaymentCardState()
}