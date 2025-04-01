package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.response.user.JwtTokenResponse
import com.ssafy.locket.data.network.response.user.UserInfoResponse
import com.ssafy.locket.data.network.request.user.UserJoinRequest
import com.ssafy.locket.data.network.request.user.UserLoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface UserService {
    @GET("users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: Long) : Response<UserInfoResponse>
}