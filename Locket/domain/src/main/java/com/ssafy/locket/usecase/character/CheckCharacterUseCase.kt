package com.ssafy.locket.usecase.character

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.CharacterResult
import com.ssafy.locket.model.home.character.CharacterStatus
import com.ssafy.locket.repository.home.character.CharacterRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CheckCharacterUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
) {
    suspend operator fun invoke(): Flow<ResponseStatus<CharacterResult>> = flow {
        characterRepository.isCharacterExist().collect { status ->
            when(status) {
                is ResponseStatus.Success -> {
                    if(status.data.characterExist) {
                        characterRepository.getCharacter().collect { info ->
                            when(info) {
                                is ResponseStatus.Success -> {
                                    emit(ResponseStatus.Success(CharacterResult.Exist(info.data)))
                                }
                                is ResponseStatus.Error -> {
                                    emit(ResponseStatus.Error(info.error))
                                }
                            }
                        }
                    } else {
                        emit(ResponseStatus.Success(CharacterResult.NotExist(status.data)))
                    }
                }
                is ResponseStatus.Error -> {
                    emit(ResponseStatus.Error(status.error))
                }
            }
        }
    }
}