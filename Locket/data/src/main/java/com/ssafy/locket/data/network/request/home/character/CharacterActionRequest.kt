package com.ssafy.locket.data.network.request.home.character

import com.google.gson.annotations.SerializedName

data class CharacterActionRequest(
    val actionType: String,
    @SerializedName("characterName") val name: String
)