package com.ssafy.locket.data.repository.auth

import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.AuthService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.request.auth.UserJoinRequest
import com.ssafy.locket.data.network.request.auth.UserLoginRequest
import com.ssafy.locket.data.network.response.auth.JwtTokenResponse.Companion.toDomainModel
import com.ssafy.locket.model.auth.JwtToken
import com.ssafy.locket.model.auth.UserJoin
import com.ssafy.locket.model.auth.UserLogin
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val dataStore: UserDataStoreSource
): AuthRepository {
    override suspend fun join(
        birthYear: Int,
        fingerprintRegistered: Boolean,
        paymentPassword: Int,
        userJob: String,
        kakaoAccessToken: String
    ): Flow<ResponseStatus<JwtToken>> {
        return flow {
            ApiResponseHandler().handle {
                val fcmToken = dataStore.fcmToken.first() ?: ""
                authService.signUp(UserJoinRequest(
                    birthYear = birthYear,
                    fcmToken = fcmToken,
                    fingerprintRegistered = fingerprintRegistered,
                    kakaoAccessToken = kakaoAccessToken,
                    paymentPassword = paymentPassword,
                    userJob = userJob
                ))
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        dataStore.saveUserId(result.data.userId.toLong())
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }
                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun login(accessToken: String): Flow<ResponseStatus<JwtToken>> {
        return flow {
            ApiResponseHandler().handle {
                val fcmToken = dataStore.fcmToken.first() ?: ""
                authService.login(UserLoginRequest(
                    accessToken = accessToken,
                    fcmToken = fcmToken
                ))
            }.onEach { result ->
                when(result) {
                    is ApiResponse.Success -> {
                        dataStore.saveUserId(result.data.userId.toLong())
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