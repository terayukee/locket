package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.request.payment.PassswordVerifyRequest
import com.ssafy.locket.data.network.request.payment.PaymentRequest
import com.ssafy.locket.data.network.response.payment.CardListResponse
import com.ssafy.locket.data.network.response.payment.CheckFingerprintResponse
import com.ssafy.locket.data.network.response.payment.PasswordVerifyResponse
import com.ssafy.locket.data.network.response.payment.PaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface PaymentService {
    @POST("payment/api/payment/nfc")
    suspend fun pay(@Query("userId") userId: Long, @Body paymentRequest: PaymentRequest): Response<PaymentResponse>

    @POST("payment/api/payment/auth/verify-password")
    suspend fun verifyPassword(@Query("userId") userId: Long, @Body passswordVerifyRequest: PassswordVerifyRequest): Response<PasswordVerifyResponse>

    @GET("payment/api/payment/cards")
    suspend fun getCards(@Query("userId") userId: Long): Response<CardListResponse>

    @GET("payment/api/payment/auth-info/fingerprint")
    suspend fun checkFingerPrintRegistered(@Query("userId") userId: Long): Response<CheckFingerprintResponse>
}