package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.response.CharacterInfoResponse
import retrofit2.Response
import retrofit2.http.GET

internal interface CharacterService {
    @GET("pet/info")
    suspend fun getCharacterInfo(): Response<CharacterInfoResponse>
}