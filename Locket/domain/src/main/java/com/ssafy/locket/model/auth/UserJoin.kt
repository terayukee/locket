package com.ssafy.locket.model.auth

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserJoin (
    val accesstoken: String,
    val birthYear: Int,
    val fingerprintRegistered: Boolean,
    val paymentPassword: Int,
    val userJob: String
): BaseModel