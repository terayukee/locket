package com.ssafy.locket.presentation.home.character.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.usecase.character.GetCharacterInfoUseCase
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
class CharacterViewModel @Inject constructor(
    private val getCharacterInfoUseCase: GetCharacterInfoUseCase
): ViewModel() {
    private var _characterInfo = MutableStateFlow<CharacterInfoState>(CharacterInfoState.Initial)
    val characterInfo: Flow<CharacterInfoState> = _characterInfo.asStateFlow()

    fun setLoading() {
        _characterInfo.value = CharacterInfoState.Loading
    }

    fun getCharacterInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            getCharacterInfoUseCase()
                .onStart { setLoading() }
                .catch { e ->

                }
                .collect { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            _characterInfo.value = CharacterInfoState.Success(uiState.data)
                        }
                        is ResponseStatus.Error -> {
                            _characterInfo.value = CharacterInfoState.Error(uiState.error.message)
                        }
                    }
                }
        }
    }
}

sealed class CharacterInfoState {
    object Initial: CharacterInfoState()
    object Loading: CharacterInfoState()
    data class Success(val characterInfo: CharacterInfo): CharacterInfoState()
    data class Error(val message: String): CharacterInfoState()
}