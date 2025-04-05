package com.ssafy.locket.data.network.api

import com.ssafy.locket.model.finance.budget.feedback.Feedback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AnalysisService {
    @GET("users/feedback")
    suspend fun getFeedback(@Query("userId") userId: Int, @Query("year") year:Int, @Query("month") month:Int) : Response<Feedback>
}