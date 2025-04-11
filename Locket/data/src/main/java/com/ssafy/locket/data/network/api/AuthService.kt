package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.request.auth.UserJoinRequest
import com.ssafy.locket.data.network.request.auth.UserLoginRequest
import com.ssafy.locket.data.network.response.auth.JwtTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("users/signup")
    suspend fun signUp(@Body userJoinRequest: UserJoinRequest) : Response<JwtTokenResponse>

    @POST("users/login")
    suspend fun login(@Body userLoginRequest: UserLoginRequest) : Response<JwtTokenResponse>
}