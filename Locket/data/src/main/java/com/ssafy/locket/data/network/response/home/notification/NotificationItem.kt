package com.ssafy.locket.data.network.response.home.notification

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.home.Notification
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationItem(
    val alertId: Long,
    val formattedDate: String,
    val message: String,
    val read: Boolean,
    val type: String,
    val productId: Int,
    val alertPrice: Int
) : BaseResponse {
    companion object : DataMapper<NotificationItem, Notification> {
        override fun NotificationItem.toDomainModel(): Notification {
            return Notification(
                id = this.alertId,
                type = this.type,
                alertPrice = this.alertPrice,
                content = this.message,
                date = this.formattedDate,
                productId = this.productId
            )
        }
    }
}