package com.ssafy.locket.data.remote.api

import com.ssafy.locket.data.remote.request.RegisterUserReq
import com.ssafy.locket.data.remote.response.RegisterUserRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("api/users/signup")
    suspend fun registerUser(@Body request: RegisterUserReq): Response<RegisterUserRes>


}