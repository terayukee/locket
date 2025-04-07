package com.ssafy.locket.data.network.response.finance.analysis

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.finance.analysis.CategoryBreakdownResponse.Companion.toDomainModel
import com.ssafy.locket.model.finance.budget.feedback.CategoryBreakdownList
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryBreakdownListResponse(
    val categoryBreakdownList: List<CategoryBreakdownResponse>
): BaseResponse {
    companion object: DataMapper<CategoryBreakdownListResponse, CategoryBreakdownList> {
        override fun CategoryBreakdownListResponse.toDomainModel(): CategoryBreakdownList {
            return CategoryBreakdownList(
                categoryBreakdownList = this.categoryBreakdownList.map { it.toDomainModel() }?: emptyList()
            )
        }
    }
}
