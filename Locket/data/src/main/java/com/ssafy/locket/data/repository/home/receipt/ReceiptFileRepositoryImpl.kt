package com.ssafy.locket.data.repository.home.receipt

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
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
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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

        if(isFileLargerThanMaxMB(context, uri) && type == "image") return compressImageFile(context, uri)
        else {
            val tempFile = File(context.cacheDir, fileName)
            val inputStream = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(tempFile)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            return tempFile
        }
    }

    private fun isFileLargerThanMaxMB(context: Context, uri: Uri): Boolean {
        val fileSizeInBytes = getFileSize(context, uri)
        val maxSizeInBytes = 4 * 1024 * 1024 // 4MB in bytes
        return fileSizeInBytes > maxSizeInBytes
    }

    private fun getFileSize(context: Context, uri: Uri): Long {
        return if (uri.scheme == "content") {
            // ContentResolver를 통해 Uri의 파일 크기 가져오기
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            var size: Long = 0
            cursor?.use {
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIndex != -1) {
                    it.moveToFirst()
                    size = it.getLong(sizeIndex) // 파일 크기 (바이트 단위)
                }
            }
            size
        } else if (uri.scheme == "file") {
            // File 객체를 통해 파일 크기 가져오기
            File(uri.path!!).length()
        } else {
            0L
        }
    }

    private fun compressImageFile(context: Context, uri: Uri): File {
        val maxSizeMB: Int = 4
        val contentResolver = context.contentResolver
        val fileName = "locket_${System.currentTimeMillis()}.jpg"
        val tempFile = File(context.cacheDir, fileName)

        // 1. URI를 Bitmap으로 변환 (메모리 최적화)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true // 메타데이터만 읽기
        }
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        }

        // 2. 이미지 크기 계산 (원본 크기 → 1/2, 1/4 등으로 줄임)
        var sampleSize = 1
        while ((options.outWidth * options.outHeight) / (sampleSize * sampleSize) > 1024 * 1024) {
            sampleSize *= 2
        }

        // 3. 실제 Bitmap 디코딩 (크기 조정)
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = Bitmap.Config.RGB_565 // 메모리 50% 절약
        }
        val bitmap = contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, decodeOptions)
        } ?: throw IOException("Bitmap 변환 실패")

        // 4. 품질 조정하며 4MB 이하로 압축
        var quality = 90
        val outputStream = ByteArrayOutputStream()
        do {
            outputStream.reset() // 초기화
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5 // 품질 5%씩 감소
        } while (outputStream.size() > maxSizeMB * 1024 * 1024)

        // 5. 압축된 데이터를 파일로 저장
        tempFile.outputStream().use { fos ->
            fos.write(outputStream.toByteArray())
        }

        return tempFile

    }

    private fun createMultipartFromFile(type: String, file: File): MultipartBody.Part {
        val requestBody =
            if (type == "image") file.asRequestBody("image/*".toMediaTypeOrNull()) else file.asRequestBody(
                "application/pdf".toMediaTypeOrNull()
            )
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }

    fun compressToTargetSize(bitmap: Bitmap, targetMB: Int): ByteArray {
        var quality = 90
        var outputStream = ByteArrayOutputStream()

        do {
            outputStream.reset() // 초기화
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 10 // 품질 10%씩 감소
        } while (outputStream.size() > targetMB * 1024 * 1024)

        return outputStream.toByteArray()
    }

}