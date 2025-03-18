package com.ssafy.locket.data.remote.request

data class RegisterUserReq(
    val birth_date: String,
    val email: String,
    val login_id: String,
    val nickname: String,
    val password: String
)