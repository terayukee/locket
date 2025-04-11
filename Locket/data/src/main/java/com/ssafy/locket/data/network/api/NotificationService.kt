package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.response.home.notification.NotificationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationService {
    @GET("users/notifications/all")
    suspend fun getNotificationAll(@Query("userId") userId: Int) : Response<NotificationResponse>
}