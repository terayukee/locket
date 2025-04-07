package com.ssafy.locket.data.network.response.finance.analysis

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.finance.analysis.CategoryBreakdownListResponse.Companion.toDomainModel
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class FeedbackResponse(
    val categoryBreakdownList: CategoryBreakdownListResponse,
    val characterImageUrl: String,
    val characterName: String,
    val dominantCategory: String,
    val improvement: List<String>,
    val patternAnalysis: List<String>,
    val summary: String,
    val totalAmount: BigDecimal
): BaseResponse {
    companion object: DataMapper<FeedbackResponse, Feedback> {
        override fun FeedbackResponse.toDomainModel(): Feedback {
            return Feedback(
                categoryBreakdownList = this.categoryBreakdownList.toDomainModel(),
                characterName = this.characterName,
                dominantCategory = this.dominantCategory,
                improvement = this.improvement,
                patternAnalysis = this.patternAnalysis,
                summary = this.summary,
                totalAmount = this.totalAmount
            )
        }

    }
}