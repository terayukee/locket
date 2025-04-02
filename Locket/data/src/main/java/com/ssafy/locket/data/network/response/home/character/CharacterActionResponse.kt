package com.ssafy.locket.data.network.response.home.character

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.character.CharacterGrowth
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterActionResponse(
    val characterName: String,
    val currentExp: Int,
    val expGained: Int,
    val expPercentage: Int,
    val level: Int,
    val levelUp: Boolean,
    val previousExp: Int
): BaseResponse {
    companion object: DataMapper<CharacterActionResponse,CharacterGrowth> {
        override fun CharacterActionResponse.toDomainModel(): CharacterGrowth {
            return CharacterGrowth(
                name = this.characterName,
                currentExp = this.currentExp,
                expPercentage = this.expPercentage,
                level = this.level,
                levelUp = this.levelUp
            )
        }

    }
}