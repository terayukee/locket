package com.ssafy.locket.repository.auth

import com.ssafy.locket.model.auth.JwtToken
import com.ssafy.locket.model.auth.UserJoin
import com.ssafy.locket.model.auth.UserLogin
import com.ssafy.locket.model.base.ResponseStatus
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun join(
        birthYear: Int,
        fingerprintRegisterd: Boolean,
        nickname: String,
        paymentPassword: Int,
        userJob: String
    ): Flow<ResponseStatus<JwtToken>>

    suspend fun login(accessToken: String, fcmToken: String): Flow<ResponseStatus<JwtToken>>
}