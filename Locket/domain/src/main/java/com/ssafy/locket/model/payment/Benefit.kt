package com.ssafy.locket.model.payment

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Benefit(
    val benefitDetail: String,
    val benefitId: Int,
    val item: String
) : BaseModel