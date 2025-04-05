package com.ssafy.locket.presentation.payment.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.ssafy.locket.model.payment.PaymentCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "SelectedPaymentCardView"
@HiltViewModel
class SelectedPaymentCardViewModel @Inject constructor(
): ViewModel(){
    private var _selectedPaymentCard = MutableStateFlow<SelectedPaymentCardState>(SelectedPaymentCardState.Initial)
    val selectedPaymentCard: Flow<SelectedPaymentCardState> = _selectedPaymentCard.asStateFlow()

    fun selectPaymentCard(paymentCard: PaymentCard) {
        Log.d(TAG, "selectPaymentCard: card selected ${paymentCard.cardName}")
        _selectedPaymentCard.value = SelectedPaymentCardState.Selected(paymentCard)
    }

    fun clearPaymentCard() {
        _selectedPaymentCard.value = SelectedPaymentCardState.Initial
    }
}

sealed class SelectedPaymentCardState {
    object Initial: SelectedPaymentCardState()
    data class Selected(val paymentCard: PaymentCard): SelectedPaymentCardState()
}