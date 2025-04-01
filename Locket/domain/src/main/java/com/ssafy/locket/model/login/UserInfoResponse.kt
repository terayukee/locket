package com.ssafy.locket.model.login

data class UserInfoResponse(
    val birthYear: Int,
    val nickname: String,
    val userId: Int,
    val userJob: String
)