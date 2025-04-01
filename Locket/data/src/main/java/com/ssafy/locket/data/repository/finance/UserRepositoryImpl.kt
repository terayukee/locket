package com.ssafy.locket.data.repository.finance

import android.util.Log
import com.ssafy.locket.data.network.api.UserService
import com.ssafy.locket.data.network.response.emitApiResponse
import com.ssafy.locket.model.ApiResponse
import com.ssafy.locket.model.login.UserInfoResponse
import com.ssafy.locket.repository.finance.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl
@Inject
constructor(private val userService: UserService) : UserRepository {
    override suspend fun getUserInfo(user_id: Int) : Flow<ApiResponse<UserInfoResponse>> =
        flow {
            val response = emitApiResponse(
                apiResponse = { userService.getUser(user_id) },  // ✅ Response<UserInfoResponse> 반환
                default = UserInfoResponse(0, "", 0, ""),  // ✅ default 값 유지
            )
            Log.d("UserRepository", "getUserInfo: $response")  // ✅ 디버깅 로그 추가
            emit(response)
        }

}