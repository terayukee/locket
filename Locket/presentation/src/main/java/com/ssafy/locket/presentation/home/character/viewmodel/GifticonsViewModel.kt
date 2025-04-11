package com.ssafy.locket.presentation.home.character.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.Gifticon
import com.ssafy.locket.model.home.character.GifticonList
import com.ssafy.locket.usecase.home.character.GetAllGifticonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "GifticonsViewModel"
@HiltViewModel
class GifticonsViewModel @Inject constructor(
    private val getAllGifticonsUseCase: GetAllGifticonsUseCase
): ViewModel() {
    private var _gifticonList = MutableStateFlow<GifticonState>(GifticonState.Initial)
    val gifticonList: Flow<GifticonState> = _gifticonList.asStateFlow()

    private val _selectedGifticon = MutableStateFlow<SelectedGifticonState>(SelectedGifticonState.Initial)
    val selectedGifticon: StateFlow<SelectedGifticonState> = _selectedGifticon.asStateFlow()

    fun getAllGifticons() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllGifticonsUseCase()
                .onStart { _gifticonList.value = GifticonState.Loading }
                .catch { e ->
                    Log.d(TAG, "getAllGifticons: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _gifticonList.value = GifticonState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _gifticonList.value = GifticonState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun selectGifticon(gifticon: Gifticon) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedGifticon.value = SelectedGifticonState.Selected(gifticon)
        }
    }

    fun clearGifticon() {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedGifticon.value = SelectedGifticonState.Initial
        }
    }
}

sealed class GifticonState {
    object Initial: GifticonState()
    object Loading: GifticonState()
    data class Success(val gifticonList: GifticonList): GifticonState()
    data class Error(val message: String): GifticonState()
}

sealed class SelectedGifticonState {
    object Initial: SelectedGifticonState()
    data class Selected(val gifticon: Gifticon): SelectedGifticonState()
    data class Error(val message: String): SelectedGifticonState()
}