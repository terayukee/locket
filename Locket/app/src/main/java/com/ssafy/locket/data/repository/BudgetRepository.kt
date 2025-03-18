package com.ssafy.locket.data.repository

import com.ssafy.locket.data.remote.api.BudgetService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(private val budgetService: BudgetService){
}