package com.ssafy.locket.data.remote.request

data class SignInUserReq(
    val login_id: String,
    val password: String
)