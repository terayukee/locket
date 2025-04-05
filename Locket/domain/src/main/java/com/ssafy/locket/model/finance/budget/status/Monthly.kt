package com.ssafy.locket.model.finance.budget.status

data class Monthly(
    val month: Int,
    val progress: Int,
    val remaining: Int,
    val spent: Int,
    val target: Int,
    val year: Int
)