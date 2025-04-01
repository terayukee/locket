package com.ssafy.locket.data.network.response.auth

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.user.UserInfo
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfoResponse(
    val birthYear: Int,
    val nickname: String,
    val userId: Int,
    val userJob: String
): BaseResponse {
    companion object: DataMapper<UserInfoResponse, UserInfo> {
        override fun UserInfoResponse.toDomainModel(): UserInfo {
            return UserInfo(
                birthYear = this.birthYear,
                nickname = this.nickname,
                userId = this.userId,
                userJob = this.userJob
            )
        }
    }
}