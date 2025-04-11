package com.ssafy.locket.presentation.home.receipt.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.receipt.Receipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.usecase.home.receipt.SaveReceiptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ProcessedReceiptViewMod"
// 받아온 영수증 수정 후에 저장할 때 쓸 viewmodel, 얘는 가능하면 activityViewModel 말고 viewmodel로 쓸래
@HiltViewModel
class ProcessedReceiptViewModel @Inject constructor(
    private val saveReceiptUseCase: SaveReceiptUseCase,
): ViewModel() {

    private val _savedState = Channel<SavedReceiptState>(Channel.BUFFERED)
    val savedState = _savedState.receiveAsFlow()

    fun saveReceipt(transactionId: String, storeName: String, items: List<ReceiptDetail>, totalAmount: Int, categoryAmount: Map<String, Double>) {
        viewModelScope.launch {
            saveReceiptUseCase(transactionId, storeName, items, totalAmount, categoryAmount)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "saveReceipt: ${e.message}")
                    _savedState.trySend(SavedReceiptState.Error(e.message ?: "알 수 없는 오류가 발생했습니다"))
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            Log.d(TAG, "saveReceipt: success")
                            _savedState.trySend(SavedReceiptState.Success)

                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG, "saveReceipt: error ${status.error.message}")
                            _savedState.trySend(SavedReceiptState.Error(status.error.message))
                        }
                    }
                }
        }
    }
}

sealed class SavedReceiptState {
    object Initial : SavedReceiptState()
    object Success: SavedReceiptState()
    data class Error(val message: String) : SavedReceiptState()
}