package com.ssafy.locket.model.home.character

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
class GifticonList(
    val gifticons: List<Gifticon>
) : BaseModel