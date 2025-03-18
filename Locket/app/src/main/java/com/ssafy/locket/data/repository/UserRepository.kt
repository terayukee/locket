package com.ssafy.locket.data.repository

import com.ssafy.locket.data.remote.api.UserService
import com.ssafy.locket.data.remote.request.RegisterUserReq
import com.ssafy.locket.data.remote.response.RegisterUserRes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val userService: UserService) {
    suspend fun registerUser(request: RegisterUserReq): RegisterUserRes? {
        return try {
            val response = userService.registerUser(request)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}