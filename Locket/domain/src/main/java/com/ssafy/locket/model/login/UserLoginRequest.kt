package com.ssafy.locket.model.login

data class UserLoginRequest(
    val accessToken: String,
    val fcmToken: String
)