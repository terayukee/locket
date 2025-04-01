package com.ssafy.locket.data.network.request.user

data class UserLoginRequest(
    val accessToken: String,
    val fcmToken: String
)