package com.ssafy.locket.data.remote.api

import com.ssafy.locket.data.remote.response.analysis.AnalysisUserRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ReceiptService {
    @GET("analysis/spending/{user_id}")
    suspend fun getUserAnalysis(@Path("user_id") user_id: Int) : Response<AnalysisUserRes>
}