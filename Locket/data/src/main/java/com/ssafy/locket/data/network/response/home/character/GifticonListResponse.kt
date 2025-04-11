package com.ssafy.locket.data.network.response.home.character

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.home.character.GifticonResponse.Companion.toDomainModel
import com.ssafy.locket.model.home.character.GifticonList
import kotlinx.parcelize.Parcelize

@Parcelize
class GifticonListResponse(
    @SerializedName("rewards")
    val gifticonList: List<GifticonResponse>
): BaseResponse {
    companion object: DataMapper<GifticonListResponse, GifticonList> {
        override fun GifticonListResponse.toDomainModel(): GifticonList {
            return GifticonList(
                gifticons = this.gifticonList.map { it.toDomainModel() }?: emptyList()
            )
        }
    }
}