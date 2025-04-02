package com.ssafy.locket.data.network.request.auth

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.model.base.BaseModel

data class UserJoinRequest(
    val birthYear: Int,
    val fcmToken: String,
    val fingerprintRegistered: Boolean,
    @SerializedName("accessToken") val kakaoAccessToken: String,
    val paymentPassword: Int,
    val userJob: String
)