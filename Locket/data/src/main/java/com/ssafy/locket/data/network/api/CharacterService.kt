package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.request.home.character.CharacterActionRequest
import com.ssafy.locket.data.network.response.home.character.CharacterActionResponse
import com.ssafy.locket.data.network.response.home.character.CharacterInfoResponse
import com.ssafy.locket.data.network.response.home.character.CharacterStatusResponse
import com.ssafy.locket.data.network.response.home.character.GifticonListResponse
import com.ssafy.locket.data.network.response.home.character.GifticonResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface CharacterService {
    @GET("users/pet")
    suspend fun isCharacterExist(@Query("userId") userId: Long): Response<CharacterStatusResponse>

    @POST("users/pet")
    suspend fun createCharacter(@Query("userId") userId: Long): Response<CharacterInfoResponse>

    @GET("users/pet/{userId}")
    suspend fun getCharacterInfo(@Path("userId") userId: Long): Response<CharacterInfoResponse>

    @GET("users/pet/{userId}/rewards")
    suspend fun getAllGifticons(@Path("userId") userId: Long): Response<GifticonListResponse>

    @POST("users/pet/{userId}/experience")
    suspend fun growCharacter(@Path("userId") userId: Long, @Body characterActionRequest: CharacterActionRequest): Response<CharacterActionResponse>

    @POST("users/pet/{userId}/complete")
    suspend fun completeCharacter(@Path("userId") userId: Long): Response<GifticonResponse>
}