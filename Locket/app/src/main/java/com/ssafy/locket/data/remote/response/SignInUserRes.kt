package com.ssafy.locket.data.remote.response

data class SignInUserRes(
    val message: String,
    val token: String,
    val user_id: Int
)