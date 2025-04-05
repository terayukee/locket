package com.ssafy.locket.model.home

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
    val id: Int,
    val type: String,
    val alertPrice: String?,
    val content: String,
    val date: String
) : BaseModel
