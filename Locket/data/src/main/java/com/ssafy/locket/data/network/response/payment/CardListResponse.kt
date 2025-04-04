package com.ssafy.locket.data.network.response.payment

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.payment.CardResponse.Companion.toDomainModel
import com.ssafy.locket.model.payment.PaymentCardList
import kotlinx.parcelize.Parcelize

@Parcelize
data class CardListResponse(
    @SerializedName("cardList") val cards: List<CardResponse>,
    val count: Int
): BaseResponse {
    companion object: DataMapper<CardListResponse, PaymentCardList> {
        override fun CardListResponse.toDomainModel(): PaymentCardList {
            return PaymentCardList(
                cards = this.cards.map { it.toDomainModel() }?: emptyList(),
                count = this.count
            )
        }
    }
}