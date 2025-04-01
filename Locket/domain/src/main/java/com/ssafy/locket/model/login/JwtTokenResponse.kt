package com.ssafy.locket.model.login

data class JwtTokenResponse(
    val access_token: String,
    val refresh_token: String
)