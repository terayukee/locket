package com.ssafy.locket.data.remote.response

data class AnalysisUserRes(
    val categories: List<Category>,
    val highest_spending_store: HighestSpendingStore,
    val message: String,
    val most_frequent_store: String,
    val spending_trend: List<SpendingTrend>,
    val status: Int,
    val total_spending: Int,
    val user_id: String
)