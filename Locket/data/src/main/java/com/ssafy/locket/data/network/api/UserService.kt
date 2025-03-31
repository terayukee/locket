package com.ssafy.locket.data.network.api

import com.ssafy.locket.model.login.UserJoinRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface UserService {
    @POST("users/signup")
    suspend fun signUp(@Body userJoinRequest: UserJoinRequest) : Response<Unit>




}