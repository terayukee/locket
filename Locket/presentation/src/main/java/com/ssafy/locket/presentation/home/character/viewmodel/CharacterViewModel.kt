package com.ssafy.locket.presentation.home.character.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterAction
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.CharacterResult
import com.ssafy.locket.usecase.character.CheckCharacterExistUseCase
import com.ssafy.locket.usecase.character.CheckCharacterUseCase
import com.ssafy.locket.usecase.character.CreateCharacterUseCase
import com.ssafy.locket.usecase.character.GetCharacterInfoUseCase
import com.ssafy.locket.usecase.character.GrowCharacterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CharacterViewModel"
@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val getCharacterInfoUseCase: GetCharacterInfoUseCase,
    private val checkCharacterExistUseCase: CheckCharacterExistUseCase,
    private val createCharacterUseCase: CreateCharacterUseCase,
    private val checkCharacterUseCase: CheckCharacterUseCase,
    private val growCharacterUseCase: GrowCharacterUseCase
): ViewModel() {
    private var _characterInfo = MutableStateFlow<CharacterInfoState>(CharacterInfoState.Initial)
    val characterInfo: Flow<CharacterInfoState> = _characterInfo.asStateFlow()

    fun setLoading() {
        _characterInfo.value = CharacterInfoState.Loading
    }

    fun checkCharacter() {
        viewModelScope.launch(Dispatchers.IO) {
            checkCharacterUseCase()
                .onStart { setLoading() }
                .catch { e ->
                    Log.d(TAG, "checkCharacter: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            when(status.data) {
                                is CharacterResult.NotExist -> {
                                    _characterInfo.value = CharacterInfoState.Empty
                                }
                                is CharacterResult.Exist -> {
//                                    _characterInfo.value = CharacterInfoState.Success(status.data.info)
                                    _characterInfo.value = mapCharacterResultToState(status.data) // 이게 되나?
                                }
                                is CharacterResult.Error -> {
//                                    _characterInfo.value = CharacterInfoState.Error((status.data as CharacterResult.Error).message)
                                    _characterInfo.value = mapCharacterResultToState(status.data) // 이게 되나?
                                }
                            }
                        }
                        is ResponseStatus.Error -> {
                            _characterInfo.value = CharacterInfoState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun growCharacter(actionType: CharacterAction) {
        viewModelScope.launch(Dispatchers.IO) {
            growCharacterUseCase(actionType.name, (_characterInfo.value as CharacterInfoState.Success).characterInfo.name)
                .onStart {  }
                .catch { e ->
                    Log.d(TAG, "growCharacter: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _characterInfo.update { currentState ->
                                if(currentState is CharacterInfoState.Success) {
                                    val updateInfo = currentState.characterInfo.copy(
                                        level = status.data.level,
                                        exp = status.data.currentExp,
                                        expPercentage = status.data.expPercentage
                                    )
                                    CharacterInfoState.Success(updateInfo)
                                } else currentState
                            }
                        }
                        is ResponseStatus.Error -> {
                            _characterInfo.value = CharacterInfoState.Error(status.error.message)
                        }
                    }
                }
        }
    }
    fun checkCharacterExist() {
        viewModelScope.launch(Dispatchers.IO) {
            checkCharacterExistUseCase()
                .onStart { setLoading() }
                .catch { e ->
                    Log.d(TAG, "checkCharacterExist: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            if (status.data.characterExist) getCharacterInfo()
                            else _characterInfo.value = CharacterInfoState.Empty
                        }
                        is ResponseStatus.Error -> {
                            _characterInfo.value = CharacterInfoState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun getCharacterInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            getCharacterInfoUseCase()
                .onStart { setLoading() }
                .catch { e ->
                    Log.d(TAG, "getCharacterInfo: ")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _characterInfo.value = CharacterInfoState.Success(status.data)
                        }
                        is ResponseStatus.Error -> {
                            _characterInfo.value = CharacterInfoState.Error(status.error.message)
                        }
                    }
                }
        }
    }

    fun createCharacter() {
        viewModelScope.launch(Dispatchers.IO) {
            createCharacterUseCase()
                .onStart { setLoading() }
                .catch { e ->

                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _characterInfo.value = CharacterInfoState.Success(status.data)

                        }
                        is ResponseStatus.Error -> {
                            _characterInfo.value = CharacterInfoState.Error(status.error.message)
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
    object Empty: CharacterInfoState()
    data class Error(val message: String): CharacterInfoState()
}

fun mapCharacterResultToState(result: CharacterResult): CharacterInfoState {
    return when (result) {
        is CharacterResult.Exist -> CharacterInfoState.Success(result.info)
        is CharacterResult.NotExist -> CharacterInfoState.Empty
        is CharacterResult.Error -> CharacterInfoState.Error(result.message)
    }
}