package com.ssafy.locket.model.payment

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class PasswordVerify(
    val userId: Long,
    val valid: Boolean
): BaseModel
