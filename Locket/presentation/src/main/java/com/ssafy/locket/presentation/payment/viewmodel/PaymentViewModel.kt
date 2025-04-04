package com.ssafy.locket.presentation.payment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.payment.Payment
import com.ssafy.locket.model.payment.PaymentCard
import com.ssafy.locket.usecase.payment.PaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

private const val TAG = "PaymentViewModel"
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val paymentUseCase: PaymentUseCase
): ViewModel() {
    // TODO 결제 viewmodel 완성하기
    private val _payment = MutableStateFlow<PaymentState>(PaymentState.Initial)
    val payment = _payment.asStateFlow()

//    private var _selectedPaymentCard = MutableStateFlow<SelectedPaymentCardState>(SelectedPaymentCardState.Initial)
//    val selectedPaymentCard: Flow<SelectedPaymentCardState> = _selectedPaymentCard.asStateFlow()
//
//    fun selectPaymentCard(paymentCard: PaymentCard) {
//        _selectedPaymentCard.value = SelectedPaymentCardState.Selected(paymentCard)
//    }
//
//    fun clearPaymentCard() {
//        _selectedPaymentCard.value = SelectedPaymentCardState.Initial
//    }

    fun pay(paymentKey: String,
            cardId: Int,
            sellerId: Long,
            paymentCategory: String,
            paymentMerchant: String,
            amount: BigDecimal,
            storeName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            paymentUseCase(paymentKey, cardId, sellerId, paymentCategory, paymentMerchant, amount, storeName)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "pay: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _payment.value = PaymentState.Success(status.data)
                            Log.d(TAG, "pay: Success ${status.data}")
                        }
                        is ResponseStatus.Error -> {
                            _payment.value = PaymentState.Error(status.error.message)
                            Log.d(TAG, "pay: Error ${status.error.message}")
                        }
                    }
                }
        }

    }
}

sealed class PaymentState {
    object Initial: PaymentState()
    data class Success(val payment: Payment): PaymentState()
    data class Error(val message: String) : PaymentState()
}
