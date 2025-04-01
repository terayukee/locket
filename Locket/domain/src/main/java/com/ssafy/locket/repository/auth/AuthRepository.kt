package com.ssafy.locket.repository.auth

import com.ssafy.locket.model.auth.JwtToken
import com.ssafy.locket.model.base.ResponseStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun join(
        birthYear: Int,
        fingerprintRegistered: Boolean,
        nickname: String,
        paymentPassword: Int,
        userJob: String,
        kakaoAccessToken: String
    ): Flow<ResponseStatus<JwtToken>>
    suspend fun login(accessToken: String): Flow<ResponseStatus<JwtToken>>
}