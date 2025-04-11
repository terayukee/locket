package com.ssafy.locket.model.payment

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckFingerprint(
    val fingerprintRegistered: Boolean,
    val userId: Int
): BaseModel
