package com.ssafy.locket.data.network.response.home.character

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.character.Toy
import kotlinx.parcelize.Parcelize

@Parcelize
data class ToyResponse(
    @SerializedName("available") val isAvailable: Boolean,
    @SerializedName("remainingTimeMinutes") val remainingTimeMinutes: Int
): BaseResponse {
    companion object: DataMapper<ToyResponse, Toy> {
        override fun ToyResponse.toDomainModel(): Toy {
            return Toy(
                isAvailable = this.isAvailable,
                remainingTimeMinutes = this.remainingTimeMinutes
            )
        }
    }
}
