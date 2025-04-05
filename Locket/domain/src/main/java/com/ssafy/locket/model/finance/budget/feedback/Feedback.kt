package com.ssafy.locket.model.finance.budget.feedback

data class Feedback(
    val categoryBreakdown: List<CategoryBreakdown>,
    val insights: List<String>,
    val recommendations: List<String>,
    val summary: String,
    val totalAmount: Int
)