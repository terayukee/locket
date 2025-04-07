package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.response.finance.payment_history.PaymentDailyHistoryResponse
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyCalendarResponse
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyHistoryResponse
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyTotalResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PaymentHistoryService {
    @GET("elasticsearch/payment/month")
    suspend fun getPaymentMonthlyHistory(@Query("userId") userId: Long, @Query("year") year: Int, @Query("month") month: Int): Response<PaymentMonthlyHistoryResponse>

    @GET("elasticsearch/payment/calendar")
    suspend fun getPaymentCalendar(@Query("userId") userId: Long, @Query("year") year: Int, @Query("month") month: Int): Response<PaymentMonthlyCalendarResponse>

    @GET("elasticsearch/payment/day")
    suspend fun getPaymentDailyHistory(@Query("userId") userId: Long, @Query("year") year: Int, @Query("month") month: Int, @Query("day") day: Int): Response<PaymentDailyHistoryResponse>

    @GET("elasticsearch/payment/month/total")
    suspend fun getPaymentMonthlyTotal(@Query("userId") userId: Long, @Query("year") year: Int, @Query("month") month: Int): Response<PaymentMonthlyTotalResponse>
}