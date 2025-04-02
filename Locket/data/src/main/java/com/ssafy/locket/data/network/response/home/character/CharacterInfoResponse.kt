package com.ssafy.locket.data.network.response.home.character

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.Toy
import kotlinx.parcelize.Parcelize

@Parcelize
class CharacterInfoResponse(
    @SerializedName("characterId") val characterId: Long,
    @SerializedName("characterName") val characterName: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("exp") val exp: Int,
    @SerializedName("expPercentage") val expPercentage: Double,
    @SerializedName("foodCount") val foodCount: Int,
    @SerializedName("level") val level: Int,
    @SerializedName("totalExpForNextLevel")  val totalExpForNextLevel: Int,
    @SerializedName("toy") val toy: Toy,
    @SerializedName("userId") val userId: Long
): BaseResponse {
    companion object: DataMapper<CharacterInfoResponse, CharacterInfo> {
        override fun CharacterInfoResponse.toDomainModel(): CharacterInfo {
            return CharacterInfo(
                id = this.characterId,
                name = this.characterName,
                exp = this.exp,
                expPercentage = this.expPercentage,
                foodCount = this.foodCount,
                level = this.level,
                totalExpForNextLevel = this.totalExpForNextLevel,
                toy = this.toy,
            )
        }
    }
}