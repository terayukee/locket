package com.ssafy.locket.data.network.response.finance.analysis

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.finance.budget.feedback.CategoryBreakdown
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class CategoryBreakdownResponse(
    val amount: BigDecimal,
    val category: String,
    val percentage: Double
) : BaseResponse {
    companion object : DataMapper<CategoryBreakdownResponse, CategoryBreakdown> {
        override fun CategoryBreakdownResponse.toDomainModel(): CategoryBreakdown {
            return CategoryBreakdown(
                amount = this.amount,
                category = this.category,
                percentage = this.percentage
            )
        }
    }
}