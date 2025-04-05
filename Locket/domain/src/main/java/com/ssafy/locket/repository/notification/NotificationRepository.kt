package com.ssafy.locket.repository.notification

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.Notification
import com.ssafy.locket.model.home.NotificationListInfo
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotificationList() : Flow<ResponseStatus<NotificationListInfo>>
}