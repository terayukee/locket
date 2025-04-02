package com.ssafy.locket.usecase.auth

import com.ssafy.locket.model.auth.JwtToken
import com.ssafy.locket.model.auth.UserJoin
import com.ssafy.locket.model.auth.UserLogin
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        birthYear: Int,
        fingerprintRegisterd: Boolean,
        paymentPassword: Int,
        userJob: String,
        kakaoAccessToken: String
    ): Flow<ResponseStatus<JwtToken>> {
        return authRepository.join(
            birthYear,
            fingerprintRegisterd,
            paymentPassword,
            userJob,
            kakaoAccessToken
        )
    }
}