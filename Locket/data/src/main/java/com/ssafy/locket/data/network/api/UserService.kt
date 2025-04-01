package com.ssafy.locket.data.network.api

import com.ssafy.locket.model.login.UserInfoResponse
import com.ssafy.locket.model.login.UserJoinRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface UserService {
    @POST("users/signup")
    suspend fun signUp(@Body userJoinRequest: UserJoinRequest) : Response<Unit>
    @GET("users/{user_id}")
    suspend fun getUser(@Path("user_id") user_id: Int) : Response<UserInfoResponse>
}