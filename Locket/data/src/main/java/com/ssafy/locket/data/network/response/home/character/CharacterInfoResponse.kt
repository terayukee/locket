package com.ssafy.locket.data.network.response.home.character

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.character.CharacterInfo
import com.ssafy.locket.model.home.character.Toy
import kotlinx.parcelize.Parcelize

@Parcelize
class CharacterInfoResponse(
    val characterId: Long,
    val characterName: String,
    val createdAt: String,
    val exp: Int,
    val expPercentage: Double,
    val foodCount: Int,
    val level: Int,
    val totalExpForNextLevel: Int,
    val toy: Toy,
    val userId: Long
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