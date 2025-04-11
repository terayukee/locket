package com.ssafy.locket.model.home

import com.ssafy.locket.model.base.BaseModel
import com.ssafy.locket.model.graph.Product
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationListInfo(
    val past: List<Notification>,
    val recent:List<Notification>
) : BaseModel