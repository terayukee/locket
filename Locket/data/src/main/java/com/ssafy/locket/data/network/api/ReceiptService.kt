package com.ssafy.locket.data.network.api

import com.ssafy.locket.data.network.request.home.receipt.ProcessReceiptRequest
import com.ssafy.locket.data.network.response.home.character.CharacterStatusResponse
import com.ssafy.locket.data.network.response.home.receipt.AvailableReceiptResponse
import com.ssafy.locket.data.network.response.home.receipt.ProcessedReceiptResponse
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import com.ssafy.locket.model.home.receipt.ReceiptList
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ReceiptService {
    @GET("elasticsearch/payment/available/{userId}")
    suspend fun getAvailableReceiptList(@Path("userId") userId: Long): Response<AvailableReceiptResponse>

    @Multipart
    @POST("ai/receipt/camera/{transactionId}")
    suspend fun processReceiptImage(@Part file: MultipartBody.Part, @Path("transactionId") transactionId: String): Response<ProcessedReceiptResponse>

    @Multipart
    @POST("ai/receipt/pdf/{transactionId}")
    suspend fun processReceiptPdf(@Part file: MultipartBody.Part, @Path("transactionId") transactionId: String): Response<ProcessedReceiptResponse>

    @POST("elasticsearch/ai/save/{transactionId}")
    suspend fun saveReceipt(@Path("transactionId") transactionId: String, @Body processedReceiptRequest: ProcessReceiptRequest): Response<Unit>
}