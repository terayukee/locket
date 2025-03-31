package com.ssafy.locket.presentation.home.receipt.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val TAG = "ReceiptFileSelectionView"

@HiltViewModel
class ReceiptFileSelectionViewModel @Inject constructor() : ViewModel() {
    private val _selectedType =
        MutableStateFlow<FileTypeSelectionUiState>(FileTypeSelectionUiState.Initial)
    val selectedType: StateFlow<FileTypeSelectionUiState> = _selectedType.asStateFlow()

    private val _receiptFileSelectionUiState =
        MutableStateFlow<ReceiptFileSelectionUiState>(ReceiptFileSelectionUiState.Initial)
    val receiptFileSelectionUiState: StateFlow<ReceiptFileSelectionUiState> =
        _receiptFileSelectionUiState.asStateFlow()

    fun selectReceiptFile(uri: Uri) {
        _receiptFileSelectionUiState.value = ReceiptFileSelectionUiState.Success(uri)
        Log.d(TAG, "selectReceiptFile: 값 할당")
    }

    fun selectType(type: FileTypeSelectionUiState) {
        _selectedType.value = type
    }

    fun clearSelectedType() {
        _selectedType.value = FileTypeSelectionUiState.None
    }

    fun clearSelectedReceiptFile() {
        _receiptFileSelectionUiState.value = ReceiptFileSelectionUiState.None
    }
}

// UI State
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