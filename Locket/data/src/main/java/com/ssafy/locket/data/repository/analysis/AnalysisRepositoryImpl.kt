package com.ssafy.locket.data.repository.analysis

import android.util.Log
import com.google.gson.Gson
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.AnalysisService
import com.ssafy.locket.data.network.api.AuthService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.notification.NotificationResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.repository.analysis.AnalysisRepository
import com.ssafy.locket.repository.auth.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class AnalysisRepositoryImpl @Inject constructor(
    private val analysisService: AnalysisService,
    private val dataStore: UserDataStoreSource
): AnalysisRepository {
    override suspend fun getFeedback(year: Int, month: Int): Flow<ResponseStatus<Feedback>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                analysisService.getFeedback(userId.toInt(),year,month)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    val data = result.data
                    Log.d("NotificationRepository", "API 응답 성공: ${data}")
                    Log.d("NotificationRepository", "JSON 변환: ${Gson().toJson(data)}") // JSON 형태로 출력
                    emit(ResponseStatus.Success(data))
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