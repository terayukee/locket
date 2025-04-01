package com.ssafy.locket.model.auth

data class UserLogin(
    val accessToken: String,
    val fcmToken: String
)
