package com.ssafy.locket.data.network.response.home.character

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.character.CharacterStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterStatusResponse(
    val hasPet: Boolean
): BaseResponse {
    companion object: DataMapper<CharacterStatusResponse,CharacterStatus> {
        override fun CharacterStatusResponse.toDomainModel(): CharacterStatus {
            return CharacterStatus(
                characterExist = this.hasPet
            )
        }
    }
}