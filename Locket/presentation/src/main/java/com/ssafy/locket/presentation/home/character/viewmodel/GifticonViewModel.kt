package com.ssafy.locket.presentation.home.character.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.Gifticon
import com.ssafy.locket.model.home.character.GifticonList
import com.ssafy.locket.usecase.character.GetAllGifticonsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GifticonViewModel @Inject constructor(
    private val getAllGifticonsUseCase: GetAllGifticonsUseCase
): ViewModel() {
    private var _gifticonList = MutableStateFlow<GifticonState>(GifticonState.Initial)
    val gifticonList: Flow<GifticonState> = _gifticonList.asStateFlow()

    fun getAllGifticons() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllGifticonsUseCase()
                .onStart { _gifticonList.value = GifticonState.Loading }
                .catch { e ->

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
}

sealed class GifticonState {
    object Initial: GifticonState()
    object Loading: GifticonState()
    data class Success(val gifticonList: GifticonList): GifticonState()
    data class Error(val message: String): GifticonState()
}