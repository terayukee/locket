package com.ssafy.locket.data.repository.home.character

import android.util.Log
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.CharacterService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.request.home.character.CharacterActionRequest
import com.ssafy.locket.data.network.response.home.character.CharacterActionResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.character.CharacterInfoResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.character.CharacterStatusResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.character.GifticonListResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.character.GifticonResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.character.CharacterGrowth
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.CharacterStatus
import com.ssafy.locket.model.home.character.Gifticon
import com.ssafy.locket.model.home.character.GifticonList
import com.ssafy.locket.repository.home.character.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val TAG = "CharacterRepositoryImpl"
internal class CharacterRepositoryImpl @Inject constructor(
    private val characterService: CharacterService,
    private val dataStore: UserDataStoreSource
): CharacterRepository {
    override suspend fun getCharacter(): Flow<ResponseStatus<CharacterInfo>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                characterService.getCharacterInfo(userId)
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

    override suspend fun isCharacterExist(): Flow<ResponseStatus<CharacterStatus>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                characterService.isCharacterExist(userId)
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

    override suspend fun createCharacter(): Flow<ResponseStatus<CharacterInfo>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                Log.d(TAG, "createCharacter: $userId")
                characterService.createCharacter(userId)
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        Log.d(TAG, "createCharacter Success in impl: ${result.data.userId} ${result.data.characterName} characterId ${result.data.characterId}")
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        Log.d(TAG, "createCharacter Error in impl: ${result.error.message}")
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun getAllGifticons(): Flow<ResponseStatus<GifticonList>>{
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                characterService.getAllGifticons(userId)
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

    override suspend fun growCharacter(actionType: String, name: String): Flow<ResponseStatus<CharacterGrowth>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                characterService.growCharacter(userId, CharacterActionRequest(actionType, name))
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

    override suspend fun completeCharacter(): Flow<ResponseStatus<Gifticon>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                characterService.completeCharacter(userId)
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