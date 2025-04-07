package com.ssafy.locket.data.repository.home.receipt

import android.content.Context
import android.net.Uri
import android.util.Log
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.ReceiptService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.mapper.ReceiptDetailRequestDtoMapper
import com.ssafy.locket.data.network.request.home.receipt.ProcessReceiptDetailRequest
import com.ssafy.locket.data.network.request.home.receipt.ProcessReceiptRequest
import com.ssafy.locket.data.network.response.home.character.CharacterInfoResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.character.GifticonListResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.receipt.AvailableReceiptResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.home.receipt.ProcessedReceiptResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.receipt.ProcessedReceipt
import com.ssafy.locket.model.home.receipt.ReceiptDetail
import com.ssafy.locket.model.home.receipt.ReceiptList
import com.ssafy.locket.repository.home.receipt.ReceiptFileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

private const val TAG = "ReceiptFileRepositoryIm"

internal class ReceiptFileRepositoryImpl @Inject constructor(
    private val receiptService: ReceiptService,
    private val dataStore: UserDataStoreSource,
    @ApplicationContext private val context: Context
) : ReceiptFileRepository {
    override suspend fun getAvailableReceiptList(): Flow<ResponseStatus<ReceiptList>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                receiptService.getAvailableReceiptList(userId)
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }

                    is ApiResponse.Error -> {
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun processReceiptImage(
        uri: Uri,
        transactionId: String
    ): Flow<ResponseStatus<ProcessedReceipt>> {
        return flow {
            ApiResponseHandler().handle {
                val file = uriToImageFile("image", context, uri)
                receiptService.processReceiptImage(
                    createMultipartFromFile("image", file),
                    transactionId
                )
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }

                    is ApiResponse.Error -> {
                        Log.d(TAG, "createCharacter Error in impl: ${result.error.message}")
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun processReceiptPdfFile(
        uri: Uri,
        transactionId: String
    ): Flow<ResponseStatus<ProcessedReceipt>> {
        return flow {
            ApiResponseHandler().handle {
                val file = uriToImageFile("pdf", context, uri)
                receiptService.processReceiptPdf(
                    createMultipartFromFile("pdf", file),
                    transactionId
                )
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        emit(ResponseStatus.Success(result.data.toDomainModel()))
                    }

                    is ApiResponse.Error -> {
                        Log.d(TAG, "createCharacter Error in impl: ${result.error.message}")
                        emit(ResponseStatus.Error(result.error.toDomainModel()))
                    }
                }
            }.collect()
        }
    }

    override suspend fun saveReceipt(
        transactionId: String, storeName: String, items: List<ReceiptDetail>,
        totalAmount: Int,
        categoryAmount: Map<String, Double>
    ): Flow<ResponseStatus<Unit>> {
        return flow {
            ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                val mapper = ReceiptDetailRequestDtoMapper()
                val processReceiptDetails = items.map { mapper.map(it) }
                val processReceipt = ProcessReceiptRequest(
                    transactionId,
                    userId,
                    processReceiptDetails,
                    totalAmount,
                    categoryAmount
                )
                receiptService.saveReceipt(
                    transactionId,
                    processReceipt
                )
            }.onEach { result ->
                when (result) {
                    is ApiResponse.Success -> {
                        Log.d(TAG, "saveReceipt: Success ${result.data}")
                    }
                    is ApiResponse.Error -> {
                        Log.d(TAG, "saveReceipt Error in impl: ${result.error.message}")

                    }
                }
            }.collect()
        }
    }

    private fun uriToImageFile(type: String, context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val fileName =
            if (type == "image") "locket_${System.currentTimeMillis()}.jpg" else "locket_${System.currentTimeMillis()}.pdf"

        val tempFile = File(context.cacheDir, fileName)
        val inputStream = contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(tempFile)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return tempFile
    }

    private fun createMultipartFromFile(type: String, file: File): MultipartBody.Part {
        val requestBody =
            if (type == "image") file.asRequestBody("image/*".toMediaTypeOrNull()) else file.asRequestBody(
                "application/pdf".toMediaTypeOrNull()
            )
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }
}