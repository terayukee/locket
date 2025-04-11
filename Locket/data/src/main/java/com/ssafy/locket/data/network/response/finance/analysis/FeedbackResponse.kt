package com.ssafy.locket.data.network.response.finance.analysis

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.finance.analysis.CategoryBreakdownResponse.Companion.toDomainModel
import com.ssafy.locket.model.finance.budget.feedback.CategoryBreakdownList
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class FeedbackResponse(
    @SerializedName("categoryBreakdown")  // JSON 배열 직접 매핑
    val categoryBreakdownList: List<CategoryBreakdownResponse>,
    val insights: String,
    val recommendations: String,
    val summary: String,
    val totalAmount: BigDecimal
) : BaseResponse {
    companion object : DataMapper<FeedbackResponse, Feedback> {
        override fun FeedbackResponse.toDomainModel(): Feedback {
            return Feedback(
                categoryBreakdownList = CategoryBreakdownList(
                    this.categoryBreakdownList.map { it.toDomainModel() }
                ),
                summary = this.summary,
                totalAmount = this.totalAmount,
                recommendations = this.recommendations,
                insights = this.insights
            )
        }
    }
}