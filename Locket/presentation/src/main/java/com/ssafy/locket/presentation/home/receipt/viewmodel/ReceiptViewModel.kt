package com.ssafy.locket.presentation.home.receipt.viewmodel

import androidx.lifecycle.ViewModel
import com.ssafy.locket.model.finance.Payment
import com.ssafy.locket.model.home.ReceiptDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(

): ViewModel() {
    private val _selectedPaymentReceipt = MutableStateFlow<PaymentReceiptState>(PaymentReceiptState.Initial)
    val selectedPaymentReceipt: StateFlow<PaymentReceiptState> = _selectedPaymentReceipt.asStateFlow() // 영수증 등록할 결제 내역
    private val _receiptDetail = MutableStateFlow<ReceiptDetailState>(ReceiptDetailState.Initial)
    val receiptDetail: StateFlow<ReceiptDetailState> = _receiptDetail.asStateFlow() // ocr 처리한 영수증 정보

    fun setSelectedPaymentReceipt(payment: Payment) {
        _selectedPaymentReceipt.value = PaymentReceiptState.Selected(payment)
    }

    fun clearSelectedPayment() {
        _selectedPaymentReceipt.value = PaymentReceiptState.None
    }

    fun setReceiptDetail(receiptDetail: ReceiptDetail) {
        _receiptDetail.value = ReceiptDetailState.Selected(receiptDetail)
    }

    fun clearSelectedReceiptDetail() {
        _receiptDetail.value = ReceiptDetailState.None
    }
}

sealed class ReceiptDetailState {
    object Initial : ReceiptDetailState()
    data class Selected(val receiptDetail: ReceiptDetail) : ReceiptDetailState()
    object None : ReceiptDetailState()
}

sealed class PaymentReceiptState {
    object Initial : PaymentReceiptState()
    data class Selected(val payment: Payment) : PaymentReceiptState()
    object None : PaymentReceiptState()
}