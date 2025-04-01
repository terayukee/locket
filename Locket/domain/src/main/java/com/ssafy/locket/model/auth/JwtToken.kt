package com.ssafy.locket.model.auth

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class JwtToken(
    val accessToken: String,
    val refreshToken: String
): BaseModel
