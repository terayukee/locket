package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.request.login.UpdateUserRequest
import com.ssafy.locket.data.network.response.auth.UserInfoResponse
import com.ssafy.locket.data.network.response.mypage.DeleteUserResponse
import com.ssafy.locket.model.user.UserInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path


interface UserService {
    @GET("users/{user_id}")
    suspend fun getUser(@Path("user_id") userId: Long) : Response<UserInfoResponse>

    @PATCH("users/{user_id}")
    suspend fun updateUser(@Path("user_id") userId: Long,@Body userRequest: UpdateUserRequest) : Response<UserInfo>

    @DELETE("users/{user_id}")
    suspend fun deleteUser(@Path("user_id") userId: Long) :Response<DeleteUserResponse>
}