package com.ssafy.locket.model.finance.budget.feedback

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Feedback(
    val categoryBreakdownList: CategoryBreakdownList,
    val characterName: String,
    val dominantCategory: String,
    val improvement: List<String>,
    val patternAnalysis: List<String>,
    val summary: String,
    val totalAmount: BigDecimal
): BaseModel