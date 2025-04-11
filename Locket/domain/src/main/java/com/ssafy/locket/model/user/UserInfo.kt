package com.ssafy.locket.model.user

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    val birthYear: Int,
    val nickname: String,
    val userId: Int,
    val userJob: String
): BaseModel
