package com.ssafy.locket.repository.finance

import com.ssafy.locket.data.network.response.JwtTokenResponse
import com.ssafy.locket.data.network.response.UserInfoResponse
import com.ssafy.locket.data.network.response.UserJoinRequest
import com.ssafy.locket.data.network.response.UserLoginRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun join(userJoinRequest: com.ssafy.locket.data.network.response.UserJoinRequest, ): Flow<ApiResponse<com.ssafy.locket.data.network.response.JwtTokenResponse>>
    suspend fun getUserInfo(user_id: Int) : Flow<ApiResponse<com.ssafy.locket.data.network.response.UserInfoResponse>>
    suspend fun login(userLoginRequest: com.ssafy.locket.data.network.response.UserLoginRequest) : Flow<ApiResponse<com.ssafy.locket.data.network.response.JwtTokenResponse>>


}