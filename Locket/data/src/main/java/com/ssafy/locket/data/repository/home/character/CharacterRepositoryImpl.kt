package com.ssafy.locket.data.repository.home.character

import com.ssafy.locket.data.network.api.CharacterService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.CharacterInfoResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.repository.home.character.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class CharacterRepositoryImpl @Inject constructor(
    private val characterService: CharacterService
): CharacterRepository {
    override suspend fun getCharacter(): Flow<ResponseStatus<CharacterInfo>> {
        return flow {
            ApiResponseHandler().handle {
                characterService.getCharacterInfo()
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

}