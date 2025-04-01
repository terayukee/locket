package com.ssafy.locket.data.repository.finance

import android.util.Log
import com.ssafy.locket.data.network.api.UserService
import com.ssafy.locket.data.network.response.emitApiResponse
import com.ssafy.locket.data.network.response.user.JwtTokenResponse
import com.ssafy.locket.data.network.response.user.UserInfoResponse
import com.ssafy.locket.data.network.request.user.UserJoinRequest
import com.ssafy.locket.data.network.request.user.UserLoginRequest
import com.ssafy.locket.repository.finance.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl
@Inject
constructor(private val userService: UserService) : UserRepository {
    override suspend fun join(userJoinRequest: UserJoinRequest): Flow<ApiResponse<JwtTokenResponse>> =
      flow {
          /*val response = emitApiResponse(
              apiResponse = { userService.getUser(user_id) },  // ✅ Response<UserInfoResponse> 반환
              default = UserInfoResponse(0, "", 0, ""),  // ✅ default 값 유지
          )
          Log.d("UserRepository", "getUserInfo: $response")  // ✅ 디버깅 로그 추가
          emit(response) */
      }

    override suspend fun getUserInfo(user_id: Int) : Flow<ApiResponse<UserInfoResponse>> =
        flow {
            val response = emitApiResponse(
                apiResponse = { userService.getUser(user_id) },  // ✅ Response<UserInfoResponse> 반환
                default = UserInfoResponse(0, "", 0, ""),  // ✅ default 값 유지
            )
            Log.d("UserRepository", "getUserInfo: $response")  // ✅ 디버깅 로그 추가
            emit(response)
        }

    override suspend fun login(userLoginRequest: UserLoginRequest): Flow<ApiResponse<JwtTokenResponse>> =
        flow {
            val response = emitApiResponse(
                apiResponse = { userService.login(userLoginRequest) },
                default = JwtTokenResponse("", ""),
            )
            Log.d("SignInFragment", "login sdsdf: $response")
            emit(response)
        }
}