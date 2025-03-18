package com.ssafy.locket.data.remote.api

import com.ssafy.locket.data.remote.request.RegisterUserReq
import com.ssafy.locket.data.remote.request.SignInUserReq
import com.ssafy.locket.data.remote.response.UserRes
import com.ssafy.locket.data.remote.response.RegisterUserRes
import com.ssafy.locket.data.remote.response.SignInUserRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserService {
    @POST("users/signup")
    suspend fun registerUser(@Body request: RegisterUserReq): Response<RegisterUserRes>

    @POST("users/login")
    suspend fun signInUser(@Body request: SignInUserReq) : Response<SignInUserRes>

    @GET("users/{user_id}")
    suspend fun getUserInfo(@Path("user_id") user_id: Int) : Response<UserRes>
}