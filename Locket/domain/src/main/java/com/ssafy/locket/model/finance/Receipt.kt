package com.ssafy.locket.model.finance

import com.google.gson.annotations.SerializedName

data class Receipt(
    @SerializedName("id")
    val id: Int,
    @SerializedName("place")
    val place: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("cardName")
    val cardName: String,
    @SerializedName("price")
    val price: Int,
    val date: String
)
