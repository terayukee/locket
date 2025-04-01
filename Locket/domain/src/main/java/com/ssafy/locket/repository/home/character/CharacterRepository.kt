package com.ssafy.locket.repository.home.character

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.CharacterStatus
import kotlinx.coroutines.flow.Flow


interface CharacterRepository {
    suspend fun getCharacter(): Flow<ResponseStatus<CharacterInfo>>
    suspend fun isCharacterExist(): Flow<ResponseStatus<CharacterStatus>>
}