package com.ssafy.locket.model.finance.budget.feedback

import com.ssafy.locket.model.base.BaseModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShortFeedback(
    val feedback: String
) :BaseModel