package com.ssafy.locket.presentation.home.receipt.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import com.ssafy.locket.model.home.receipt.Receipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.presentation.finance.viewmodel.OpenDialogState
import com.ssafy.locket.presentation.home.character.viewmodel.NavigationEvent
import com.ssafy.locket.usecase.home.receipt.GetAllAvailableReceiptsUseCase
import com.ssafy.locket.usecase.home.receipt.ProcessReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
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
        _receiptFileSelectionUiState.asStateFlow() // 선택된 처리할 영수증 uri

    private val _receiptDetail = MutableStateFlow<ReceiptDetailState>(ReceiptDetailState.Initial)
    val receiptDetail: StateFlow<ReceiptDetailState> = _receiptDetail.asStateFlow() // ocr 처리한 영수증 정보

    private val _editedReceiptDetail = MutableStateFlow<ReceiptDetailState>(ReceiptDetailState.Initial)
    val editedReceiptDetail: StateFlow<ReceiptDetailState> = _receiptDetail.asStateFlow()

    private val _navigationEvent = Channel<NavigateToDetailEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

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
        _selectedType.value = FileTypeSelectionUiState.Initial
    }

    fun clearSelectedReceiptFile() {
        _receiptFileSelectionUiState.value = ReceiptFileSelectionUiState.None
    }

    fun clearSelectedReceiptDetail() {
        _receiptDetail.value = ReceiptDetailState.Initial
    }

    fun setReceiptDetail(processReceipt: ProcessedReceipt) {
        _receiptDetail.value = ReceiptDetailState.Success(processReceipt)
    }

    fun clearNavigationEvent() {
        _navigationEvent.trySend(NavigateToDetailEvent.Initial)
    }

    fun setEditReceipt() {
        _editedReceiptDetail.value = ReceiptDetailState.Success((_receiptDetail.value as ReceiptDetailState.Success).processReceipt)
//        _receiptDetail.value.let { receiptState ->
//            _editedReceiptDetail.value = receiptState
//        }
    }

    fun updateReceiptItem(updatedItem: ReceiptDetail) {
        val currentState = _editedReceiptDetail.value

        Log.d(TAG, "updateReceiptItem: state ${currentState}")
        if (currentState is ReceiptDetailState.Success) {
            val currentReceipt = currentState.processReceipt

            val updatedItems = currentReceipt.items.map { item ->
                if (item.itemId == updatedItem.itemId) updatedItem else item
            }

            val updatedCategoryAmount = recalculateCategoryAmounts(updatedItems)

            val updatedReceipt = currentReceipt.copy(
                items = updatedItems,
                categoryAmount = updatedCategoryAmount
            )

            Log.d(TAG, "updateReceiptItem: ${updatedReceipt.items}")
            _editedReceiptDetail.value = ReceiptDetailState.Success(updatedReceipt)
        }
    }

    fun setReceiptDetailEdited() {
        _editedReceiptDetail.value.let { editedState ->
            _receiptDetail.value = editedState
        }
    }

    private fun recalculateCategoryAmounts(items: List<ReceiptDetail>): Map<String, Double> {
        return items
            .filter { it.itemCategory != null }
            .groupBy { it.itemCategory!! }
            .mapValues { (_, items) ->
                items.sumOf { it.itemAmount.toDouble() * it.itemQuantity }
            }
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
                .onStart { _navigationEvent.trySend(NavigateToDetailEvent.Loading) }
                .catch { e ->
                    Log.d(TAG, "processReceipt: catch ${e.message}")
                    _receiptDetail.value = ReceiptDetailState.Error(e.message ?: "오류가 발생했습니다")
                    _navigationEvent.trySend(NavigateToDetailEvent.Error(e.message ?: "오류가 발생했습니다"))
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG, "processReceipt Success: ${status.data.storeName}")
                            _receiptDetail.value = ReceiptDetailState.Success(status.data)
                            _navigationEvent.trySend(NavigateToDetailEvent.Move)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG, "processReceipt Error: ${status.error.message}")
                            _receiptDetail.value = ReceiptDetailState.Error(status.error.message)
                            _navigationEvent.trySend(NavigateToDetailEvent.Error(status.error.message))
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
}

sealed class PaymentReceiptListState {
    object Initial : PaymentReceiptListState()
    data class Success(val paymentReceiptList: List<Receipt>) : PaymentReceiptListState()
    data class Error(val message: String) : PaymentReceiptListState()
}

sealed class ReceiptDetailState {
    object Initial : ReceiptDetailState()
    object Loading: ReceiptDetailState()
    data class Success(val processReceipt: ProcessedReceipt) : ReceiptDetailState()
    data class Error(val message: String) : ReceiptDetailState()
}

sealed class NavigateToDetailEvent {
    object Initial: NavigateToDetailEvent()
    object Move: NavigateToDetailEvent()
    object Loading: NavigateToDetailEvent()
    data class Error(val message: String): NavigateToDetailEvent()
}