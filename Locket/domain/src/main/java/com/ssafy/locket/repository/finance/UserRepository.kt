package com.ssafy.locket.repository.finance

import com.ssafy.locket.model.ApiResponse
import com.ssafy.locket.model.login.JwtTokenResponse
import com.ssafy.locket.model.login.UserInfoResponse
import com.ssafy.locket.model.login.UserJoinRequest
import com.ssafy.locket.model.login.UserLoginRequest
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface UserRepository {
    suspend fun join(userJoinRequest: UserJoinRequest, ): Flow<ApiResponse<JwtTokenResponse>>
    suspend fun getUserInfo(user_id: Int) : Flow<ApiResponse<UserInfoResponse>>
    suspend fun login(userLoginRequest: UserLoginRequest) : Flow<ApiResponse<JwtTokenResponse>>


}