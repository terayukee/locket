package com.ssafy.locket.usecase.character

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.CharacterStatus
import com.ssafy.locket.repository.home.character.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckCharacterExistUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
) {
    suspend operator fun invoke(): Flow<ResponseStatus<CharacterStatus>> {

        return characterRepository.isCharacterExist()
    }
}