package com.ssafy.locket.model.auth

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserJoin (
    val birthYear: Int,
    val fcmToken: String?,
    val fingerprintRegistered: Boolean,
    val kakaoId: Int?,
    val nickname: String,
    val paymentPassword: Int,
    val userJob: String
): BaseModel