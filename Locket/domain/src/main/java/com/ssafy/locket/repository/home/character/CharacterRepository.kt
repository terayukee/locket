package com.ssafy.locket.repository.home.character

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterInfo
import kotlinx.coroutines.flow.Flow


interface CharacterRepository {
    suspend fun getCharacter(): Flow<ResponseStatus<CharacterInfo>>
}