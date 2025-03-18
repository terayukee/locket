package com.ssafy.locket.data.remote.api


import com.ssafy.locket.data.remote.request.PaymentReq
import com.ssafy.locket.data.remote.response.PaymentRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentService {
    @POST("payments/request")
    suspend fun paymentRequest(@Body paymentReq: PaymentReq): Response<PaymentRes>

    @POST("payments/request")
    suspend fun getPaymentList(@Path("user_id") user_id: Int): Response<PaymentRes>

}