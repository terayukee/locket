package com.ssafy.locket.data.repository.user

import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.UserService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.request.login.UpdateUserRequest
import com.ssafy.locket.data.network.response.auth.UserInfoResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.mypage.DeleteUserResponse
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.user.UserInfo
import com.ssafy.locket.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val userService: UserService,
    private val dataStore: UserDataStoreSource
): UserRepository {
    override suspend fun getUserInfo(userId: Long): Flow<ResponseStatus<UserInfo>> {
        return flow {
            ApiResponseHandler().handle {
                userService.getUser(userId)
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

    override suspend fun updateUserInfo(userId: Long, userInfo: UserInfo): Flow<ResponseStatus<UserInfo>> {
        return flow {
            ApiResponseHandler().handle {
                userService.updateUser(userId, UpdateUserRequest(
                    birthYear = userInfo.birthYear,
                    nickname = userInfo.nickname,
                    userJob = userInfo.userJob
                ))
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun deleteUserInfo(userId: Long): Flow<ResponseStatus<Unit>> {
        return flow {
            ApiResponseHandler().handle {
                userService.deleteUser(userId)
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(Unit))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }
}