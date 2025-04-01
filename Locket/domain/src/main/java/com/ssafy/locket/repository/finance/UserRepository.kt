package com.ssafy.locket.repository.finance

import com.ssafy.locket.model.ApiResponse
import com.ssafy.locket.model.login.UserInfoResponse
import com.ssafy.locket.model.login.UserJoinRequest
import kotlinx.coroutines.flow.Flow

interface UserRepository {
//    suspend fun join(
//        userJoinRequest: UserJoinRequest,
//    ): Flow<ApiResponse<JwtToken>>
    suspend fun getUserInfo(user_id: Int) : Flow<ApiResponse<UserInfoResponse>>



}