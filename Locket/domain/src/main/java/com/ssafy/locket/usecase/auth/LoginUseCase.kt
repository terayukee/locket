package com.ssafy.locket.usecase.auth

import com.ssafy.locket.model.auth.JwtToken
import com.ssafy.locket.model.auth.UserLogin
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.user.UserInfo
import com.ssafy.locket.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(accessToken: String, fcmToken: String): Flow<ResponseStatus<JwtToken>> {
        return authRepository.login(accessToken, fcmToken)
    }
}