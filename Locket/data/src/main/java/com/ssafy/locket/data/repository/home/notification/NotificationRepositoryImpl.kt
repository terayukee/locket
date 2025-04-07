package com.ssafy.locket.data.repository.home.notification

import android.util.Log
import com.google.gson.Gson
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.NotificationService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.request.product.ProductLikeRequest
import com.ssafy.locket.data.network.response.auth.JwtTokenResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.notification.NotificationResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.Notification
import com.ssafy.locket.model.home.NotificationListInfo
import com.ssafy.locket.repository.notification.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationService: NotificationService,
    private val dataStore: UserDataStoreSource
): NotificationRepository {
    override suspend fun getNotificationList(): Flow<ResponseStatus<NotificationListInfo>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                notificationService.getNotificationAll(userId.toInt())
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    val data = result.data
                    Log.d("NotificationRepository", "API 응답 성공: ${data}")
                    Log.d("NotificationRepository", "JSON 변환: ${Gson().toJson(data)}") // JSON 형태로 출력
                    emit(ResponseStatus.Success(data.toDomainModel()))
                }
                is ApiResponse.Error -> {
                    val errorModel = result.error.toDomainModel()
                    Log.e("NotificationRepository", "API 에러 발생: ${errorModel.error}")
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }
}