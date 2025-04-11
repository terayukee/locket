package com.ssafy.locket.model.finance.budget.status

data class BudgetStatus(
    val budget: Budget,
    val hasBudget: Boolean,
    val userId: Int
)