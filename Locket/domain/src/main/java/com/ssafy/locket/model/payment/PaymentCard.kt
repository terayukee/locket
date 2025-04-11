package com.ssafy.locket.model.payment

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentCard(
    val cardCvc: String,
    val cardExpiry: String,
    val cardId: Int,
    val cardName: String,
    val cardNumber: String,
    val userId: Long,
    val monthlyUsage : Int,
    val benefits: List<Benefit>
): BaseModel