package com.ssafy.locket.repository.home.character

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterGrowth
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.CharacterStatus
import com.ssafy.locket.model.home.character.Gifticon
import com.ssafy.locket.model.home.character.GifticonList
import kotlinx.coroutines.flow.Flow


interface CharacterRepository {
    suspend fun getCharacter(): Flow<ResponseStatus<CharacterInfo>>
    suspend fun isCharacterExist(): Flow<ResponseStatus<CharacterStatus>>
    suspend fun createCharacter(): Flow<ResponseStatus<CharacterInfo>>
    suspend fun getAllGifticons(): Flow<ResponseStatus<GifticonList>>
    suspend fun growCharacter(actionType: String, name: String): Flow<ResponseStatus<CharacterGrowth>>
    suspend fun completeCharacter(): Flow<ResponseStatus<Gifticon>>
}