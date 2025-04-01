package com.ssafy.locket.data.network.request.auth

import com.ssafy.locket.model.base.BaseModel

data class UserJoinRequest(
    val birthYear: Int,
    val fcmToken: String,
    val fingerprintRegistered: Boolean,
    val kakaoId: Int,
    val nickname: String,
    val paymentPassword: Int,
    val userJob: String
)