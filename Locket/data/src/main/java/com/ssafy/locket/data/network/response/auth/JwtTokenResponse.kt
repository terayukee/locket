package com.ssafy.locket.data.network.response.auth

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.auth.JwtToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class JwtTokenResponse(
    val userId: Int,
    val accessToken: String,
    val refreshToken: String,
    val newUser: Boolean
): BaseResponse {
    companion object: DataMapper<JwtTokenResponse, JwtToken> {
        override fun JwtTokenResponse.toDomainModel(): JwtToken {
            return JwtToken(
                accessToken = this.accessToken,
                refreshToken = this.refreshToken
            )
        }
    }
}