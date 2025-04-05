package com.ssafy.locket.data.network.api

import com.ssafy.locket.model.finance.budget.SetBudget
import com.ssafy.locket.model.finance.budget.feedback.Feedback
import com.ssafy.locket.model.finance.budget.status.BudgetStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface BudgetService {
    @POST("users/budget/set")
    suspend fun setBudgetGoal(@Body budgetRequest: SetBudget) : Response<SetBudget>

    @GET("users/budget/status")
    suspend fun getBudgetStatus(@Query("userId") userId: Int, @Query("year") year:Int, @Query("month") month:Int): Response<BudgetStatus>

    @GET("users/feedback")
    suspend fun getFeedback(@Query("userId") userId: Int,@Query("year") year:Int,@Query("month") month:Int) : Response<Feedback>

}