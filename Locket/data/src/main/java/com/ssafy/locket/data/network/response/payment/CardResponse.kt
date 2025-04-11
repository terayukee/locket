package com.ssafy.locket.data.network.response.payment

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.graph.PriceHappinessResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.graph.ProductResponse.Companion.toDomainModel
import com.ssafy.locket.model.payment.Benefit
import com.ssafy.locket.model.payment.PaymentCard
import kotlinx.parcelize.Parcelize

private const val TAG = "CardResponse"
@Parcelize
data class CardResponse(
    @SerializedName("cardCvc") val cardCvc: String,
    @SerializedName("cardExpiry") val cardExpiry: String,
    @SerializedName("cardId") val cardId: Int,
    @SerializedName("cardName") val cardName: String,
    @SerializedName("cardNumber") val cardNumber: String,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String,
    @SerializedName("userId") val userId: Long,
    @SerializedName("monthlyUsage") val monthlyUsage : Int,
    @SerializedName("benefits") val benefits: List<Benefit>
): BaseResponse {
    companion object: DataMapper<CardResponse, PaymentCard> {
        override fun CardResponse.toDomainModel(): PaymentCard {
            return PaymentCard(
                cardCvc = this.cardCvc,
                cardExpiry = this.cardExpiry,
                cardId = this.cardId,
                cardName = this.cardName,
                cardNumber = this.cardNumber,
                userId = this.userId,
                monthlyUsage = this.monthlyUsage,
                benefits = this.benefits
            )
        }
    }
}
