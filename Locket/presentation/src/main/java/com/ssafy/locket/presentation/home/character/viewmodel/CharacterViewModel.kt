package com.ssafy.locket.presentation.home.character.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterAction
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.CharacterResult
import com.ssafy.locket.model.home.character.Toy
import com.ssafy.locket.model.home.character.characterCoolTime
import com.ssafy.locket.usecase.character.CheckCharacterUseCase
import com.ssafy.locket.usecase.character.CompleteCharacterUseCase
import com.ssafy.locket.usecase.character.CreateCharacterUseCase
import com.ssafy.locket.usecase.character.GrowCharacterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "CharacterViewModel"
@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val createCharacterUseCase: CreateCharacterUseCase,
    private val checkCharacterUseCase: CheckCharacterUseCase,
    private val growCharacterUseCase: GrowCharacterUseCase,
    private val completeCharacterUseCase: CompleteCharacterUseCase
): ViewModel() {

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _characterInfo = MutableStateFlow<CharacterInfoState>(CharacterInfoState.Initial)
    val characterInfo: StateFlow<CharacterInfoState> = _characterInfo.asStateFlow()

    private val _completeGift = MutableSharedFlow<Boolean>()
    val completeGift = _completeGift.asSharedFlow()

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
                                    _navigationEvent.emit(NavigationEvent.MoveToInitial)
                                }
                                is CharacterResult.Exist -> {
                                    _characterInfo.value = mapCharacterResultToState(status.data)
                                    _navigationEvent.emit(NavigationEvent.MoveToFragment)
                                }
                                is CharacterResult.Error -> {
                                    _characterInfo.value = mapCharacterResultToState(status.data)
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
            if(_characterInfo.value is CharacterInfoState.Success) {
                growCharacterUseCase(actionType.actionName, (_characterInfo.value as CharacterInfoState.Success).characterInfo.name)
                    .onStart {  }
                    .catch { e ->
                        Log.d(TAG, "growCharacter: ${e.message}")
                    }
                    .collect { status ->
                        when(status) {
                            is ResponseStatus.Success -> {
                                _characterInfo.update { currentState ->
                                    if(currentState is CharacterInfoState.Success) {
                                        var newFoodCount = currentState.characterInfo.foodCount
                                        if(actionType.actionName == "feed") {
                                            newFoodCount = currentState.characterInfo.foodCount -1
                                        }
                                        val updateInfo = currentState.characterInfo.copy(
                                            level = status.data.level,
                                            exp = status.data.currentExp,
                                            expPercentage = status.data.expPercentage,
                                            foodCount = newFoodCount,
                                            toy = Toy(status.data.toyAvailable, status.data.minRemain)
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
    }

    fun createCharacter() {
        viewModelScope.launch(Dispatchers.IO) {
            createCharacterUseCase()
                .onStart { setLoading() }
                .catch { e ->
                    Log.d(TAG, "createCharacter: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _characterInfo.value = CharacterInfoState.Success(status.data)
                            _navigationEvent.emit(NavigationEvent.MoveToFragment)
                            Log.d(TAG, "createCharacter Success: ${status.data}")
                        }
                        is ResponseStatus.Error -> {
                            _characterInfo.value = CharacterInfoState.Error(status.error.message)
                            Log.d(TAG, "createCharacter Error: ${status.error.message}")
                        }
                    }
                }
        }
    }

    fun completeCharacter() {
        viewModelScope.launch(Dispatchers.IO) {
            completeCharacterUseCase()
                .onStart { setLoading() }
                .catch { e ->
                    Log.d(TAG, "completeCharacter: ${e.message}")
                }
                .collect { status ->
                    when(status) {
                        is ResponseStatus.Success -> {
                            _completeGift.emit(true)
                        }
                        is ResponseStatus.Error -> {
                            Log.d(TAG, "completeCharacter Error: ${status.error.message}")
                        }
                    }
                }
        }
    }

}

sealed class NavigationEvent {
    object MoveToInitial: NavigationEvent()
    object MoveToFragment: NavigationEvent()
    data class Error(val message: String) : NavigationEvent()
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