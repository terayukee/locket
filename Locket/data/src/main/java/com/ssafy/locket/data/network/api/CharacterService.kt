package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.response.home.character.CharacterInfoResponse
import com.ssafy.locket.data.network.response.home.character.CharacterStatusResponse
import com.ssafy.locket.data.network.response.home.character.GifticonListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface CharacterService {
    @GET("pet")
    suspend fun isCharacterExist(@Path("userId") userId: Long): Response<CharacterStatusResponse>

    @GET("pet/info")
    suspend fun getCharacterInfo(): Response<CharacterInfoResponse>

    @GET("pet/info/{userId}/rewards")
    suspend fun getAllGifticons(): Response<GifticonListResponse>

//    @POST("pet/{userId}/food")
//    suspend fun postFeed(): Response<>
}