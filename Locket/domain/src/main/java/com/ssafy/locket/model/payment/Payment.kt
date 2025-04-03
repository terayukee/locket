package com.ssafy.locket.model.payment

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Payment(
    val transactionId: String,
    val status: String,
    val message: String
): BaseModel
