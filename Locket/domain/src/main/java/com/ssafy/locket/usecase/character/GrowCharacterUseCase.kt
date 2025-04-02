package com.ssafy.locket.usecase.character

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterGrowth
import com.ssafy.locket.repository.home.character.CharacterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GrowCharacterUseCase @Inject constructor(
    private val characterRepository: CharacterRepository
){
    suspend operator fun invoke(actionType: String, name: String): Flow<ResponseStatus<CharacterGrowth>> {
        return characterRepository.growCharacter(actionType, name)
    }
}