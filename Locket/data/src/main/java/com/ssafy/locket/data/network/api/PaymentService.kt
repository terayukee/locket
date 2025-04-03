package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.request.payment.CardValidationRequest
import com.ssafy.locket.data.network.request.payment.PassswordVerifyRequest
import com.ssafy.locket.data.network.request.payment.PaymentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

internal interface PaymentService {
    @POST("payment/validate-card")
    suspend fun validateCard(@Body cardValidationRequest: CardValidationRequest): Response<Unit>

    @POST("payment/nfc")
    suspend fun pay(@Query("userId") userId: Long, @Body paymentRequest: PaymentRequest): Response<Unit>

    @POST("payment/auth/verify-password")
    suspend fun verifyPassword(@Query("userId") userId: Long, @Body passswordVerifyRequest: PassswordVerifyRequest): Response<Unit>

    @GET("payment/cards")
    suspend fun getCards(@Query("userId") userId: Long): Response<Unit>
}