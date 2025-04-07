package com.ssafy.locket.data.network.response.home.character

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.character.Gifticon
import kotlinx.parcelize.Parcelize

@Parcelize
data class GifticonResponse(
    val receivedAt: String,
    val rewardId: Long,
    val rewardName: String,
    val characterName: String
):BaseResponse {
    companion object : DataMapper<GifticonResponse, Gifticon> {
        override fun GifticonResponse.toDomainModel(): Gifticon {
            return Gifticon(
                id = this.rewardId,
                name = this.rewardName,
                characterName = this.characterName
            )
        }
    }
}