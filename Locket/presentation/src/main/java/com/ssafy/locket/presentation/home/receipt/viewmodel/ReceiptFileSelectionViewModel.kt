package com.ssafy.locket.presentation.home.receipt.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.receipt.Receipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.usecase.home.receipt.GetAllAvailableReceiptsUseCase
import com.ssafy.locket.usecase.home.receipt.ProcessReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ReceiptFileSelectionView"

@HiltViewModel
class ReceiptFileSelectionViewModel @Inject constructor(
    private val getAllAvailableReceiptsUseCase: GetAllAvailableReceiptsUseCase,
    private val processReceiptUseCase: ProcessReceiptUseCase
) : ViewModel() {

    private val _receiptList = MutableStateFlow<PaymentReceiptListState>(PaymentReceiptListState.Initial) // 영수증 등록 가능한 리스트
    val receiptList: StateFlow<PaymentReceiptListState> = _receiptList.asStateFlow()

    private val _selectedType =
        MutableStateFlow<FileTypeSelectionUiState>(FileTypeSelectionUiState.Initial)
    val selectedType: StateFlow<FileTypeSelectionUiState> = _selectedType.asStateFlow() // 선택된 타입

    private val _selectedReceipt =
        MutableStateFlow<SelectedReceiptState>(SelectedReceiptState.Initial)
    val selectedReceipt: StateFlow<SelectedReceiptState> = _selectedReceipt.asStateFlow()

    private val _receiptFileSelectionUiState =
        MutableStateFlow<ReceiptFileSelectionUiState>(ReceiptFileSelectionUiState.Initial)
    val receiptFileSelectionUiState: StateFlow<ReceiptFileSelectionUiState> =
        _receiptFileSelectionUiState.asStateFlow() // 선택된 처리할 영수증


    private val _receiptDetail = MutableStateFlow<ReceiptDetailState>(ReceiptDetailState.Initial)
    val receiptDetail: StateFlow<ReceiptDetailState> = _receiptDetail.asStateFlow() // ocr 처리한 영수증 정보

    fun selectReceiptFile(uri: Uri) {
        _receiptFileSelectionUiState.value = ReceiptFileSelectionUiState.Success(uri)
    }

    fun selectType(type: FileTypeSelectionUiState) {
        _selectedType.value = type
    }

    fun selectReceipt(transactionId: String) {
        _selectedReceipt.value = SelectedReceiptState.Success(transactionId)
    }

    fun clearSelectedReceipt() {
        _selectedReceipt.value = SelectedReceiptState.None
    }

    fun clearSelectedType() {
        _selectedType.value = FileTypeSelectionUiState.None
    }

    fun clearSelectedReceiptFile() {
        _receiptFileSelectionUiState.value = ReceiptFileSelectionUiState.None
    }

    fun setReceiptDetails(receiptDetails: List<ReceiptDetail>) {
        _receiptDetail.value = ReceiptDetailState.Selected(receiptDetails)
    }

    fun clearSelectedReceiptDetail() {
        _receiptDetail.value = ReceiptDetailState.None
    }

    fun getAllAvailableReceipts() {
        viewModelScope.launch {
            getAllAvailableReceiptsUseCase()
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "getAllAvailableReceipts: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG, "getAllAvailableReceipts: receiptList")
                            _receiptList.value = PaymentReceiptListState.Success(status.data.receiptList)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG, "getAllAvailableReceipts Error: ${status.error.message}")
                        }
                    }
                }
        }
    }

    fun processReceipt(type: String, uri: Uri, transactionId: String) {
        viewModelScope.launch {
            processReceiptUseCase(type, uri, transactionId)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "processReceipt: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG, "processReceipt Success: ${status.data}")
                            _receiptDetail.value = ReceiptDetailState.Selected(status.data.items)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG, "processReceipt Error: ${status.error.message}")
                            _receiptDetail.value = ReceiptDetailState.Error(status.error.message)
                        }
                    }
                }
        }
    }
}
sealed class SelectedReceiptState {
    object Initial : SelectedReceiptState()
    object Loading : SelectedReceiptState()
    data class Success(val transactionId: String) : SelectedReceiptState()
    data class Error(val message: String) : SelectedReceiptState()
    object None : SelectedReceiptState()
}

sealed class ReceiptFileSelectionUiState {
    object Initial : ReceiptFileSelectionUiState()
    object Loading : ReceiptFileSelectionUiState()
    data class Success(val uri: Uri) : ReceiptFileSelectionUiState()
    data class Error(val message: String) : ReceiptFileSelectionUiState()
    object None : ReceiptFileSelectionUiState()
}

sealed class FileTypeSelectionUiState {
    object Initial : FileTypeSelectionUiState()
    object Image : FileTypeSelectionUiState()
    object Camera : FileTypeSelectionUiState()
    object Pdf : FileTypeSelectionUiState()
    object None : FileTypeSelectionUiState()
}

sealed class PaymentReceiptListState {
    object Initial : PaymentReceiptListState()
    data class Success(val paymentReceiptList: List<Receipt>) : PaymentReceiptListState()
    data class Error(val message: String) : PaymentReceiptListState()
}

sealed class ReceiptDetailState {
    object Initial : ReceiptDetailState()
    data class Selected(val receiptDetails: List<ReceiptDetail>) : ReceiptDetailState()
    data class Error(val message: String) : ReceiptDetailState()
    object None : ReceiptDetailState()
}