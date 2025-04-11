package com.ssafy.locket.data.network.request.auth

data class UserLoginRequest(
    val accessToken: String,
    val fcmToken: String
)