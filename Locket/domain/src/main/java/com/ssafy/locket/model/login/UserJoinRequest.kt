package com.ssafy.locket.model.login

data class UserJoinRequest(
    val birthYear: Int,
    val fcmToken: String,
    val fingerprintRegistered: Boolean,
    val kakaoId: Int,
    val nickname: String,
    val paymentPassword: Int,
    val userJob: String
)