package com.ssafy.locket.model.finance.budget.feedback

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class CategoryBreakdown(
    val amount: BigDecimal,
    val category: String,
    val percentage: Double
): BaseModel