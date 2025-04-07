package com.ssafy.locket.model.finance.budget.feedback

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CategoryBreakdownList(
    val categoryBreakdownList: List<CategoryBreakdown>
): BaseModel
