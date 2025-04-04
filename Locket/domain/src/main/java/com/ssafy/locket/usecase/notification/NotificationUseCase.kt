package com.ssafy.locket.usecase.notification

import android.util.Log
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.Notification
import com.ssafy.locket.model.home.NotificationListInfo
import com.ssafy.locket.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class NotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
){
    suspend operator fun invoke(): Flow<ResponseStatus<NotificationListInfo>> {
        return notificationRepository.getNotificationList()
    }
}