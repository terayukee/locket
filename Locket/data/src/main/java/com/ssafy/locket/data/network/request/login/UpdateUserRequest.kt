package com.ssafy.locket.data.network.request.login

data class UpdateUserRequest(
    val birthYear: Int,
    val nickname: String,
    val userJob: String
)