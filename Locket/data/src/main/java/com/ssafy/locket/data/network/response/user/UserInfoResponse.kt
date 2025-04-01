package com.ssafy.locket.data.network.response.user

data class UserInfoResponse(
    val birthYear: Int,
    val nickname: String,
    val userId: Int,
    val userJob: String
)