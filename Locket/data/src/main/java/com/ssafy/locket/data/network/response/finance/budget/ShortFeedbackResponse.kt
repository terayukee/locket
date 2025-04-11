package com.ssafy.locket.data.network.response.finance.budget

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.home.notification.NotificationItem.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.notification.NotificationResponse
import com.ssafy.locket.model.finance.budget.feedback.ShortFeedback
import com.ssafy.locket.model.home.NotificationListInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ShortFeedbackResponse(
    val feedback: String,
    val nickname: String
): BaseResponse {
    companion object : DataMapper<ShortFeedbackResponse, ShortFeedback> {
        override fun ShortFeedbackResponse.toDomainModel(): ShortFeedback {
            return ShortFeedback(
                feedback = this.feedback
            )
        }
    }
}