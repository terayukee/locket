package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.response.auth.UserInfoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface UserService {
    @GET("users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: Long) : Response<UserInfoResponse>
}