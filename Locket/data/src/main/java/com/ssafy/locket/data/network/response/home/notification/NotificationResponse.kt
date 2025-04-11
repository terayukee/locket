package com.ssafy.locket.data.network.response.home.notification

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.home.notification.NotificationItem.Companion.toDomainModel
import com.ssafy.locket.model.home.NotificationListInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationResponse(
    val past: List<NotificationItem>,
    val recent: List<NotificationItem>
) : BaseResponse{
    companion object : DataMapper<NotificationResponse, NotificationListInfo> {
        override fun NotificationResponse.toDomainModel(): NotificationListInfo {
            return NotificationListInfo(
                past = this.past.map { it.toDomainModel() },
                recent = this.recent.map { it.toDomainModel() }
            )
        }
    }
}