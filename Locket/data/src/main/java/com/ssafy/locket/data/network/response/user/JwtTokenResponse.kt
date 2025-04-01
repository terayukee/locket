package com.ssafy.locket.data.network.response.user

data class JwtTokenResponse(
    val access_token: String,
    val refresh_token: String
)