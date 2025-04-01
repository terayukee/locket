package com.ssafy.locket.repository.user

import com.ssafy.locket.model.auth.JwtToken
import com.ssafy.locket.model.auth.UserJoin
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.user.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserInfo(userId: Long): Flow<ResponseStatus<UserInfo>>
}