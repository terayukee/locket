package com.ssafy.locket.model.home.character

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Gifticon(
    val id: Long,
    val name: String
): BaseModel