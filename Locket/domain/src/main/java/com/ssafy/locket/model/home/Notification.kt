package com.ssafy.locket.model.home

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
    val id: Long,
    val date: String,
    val content: String,
    val type: String,
    val productId: Int,
    val alertPrice: Int
) : BaseModel
