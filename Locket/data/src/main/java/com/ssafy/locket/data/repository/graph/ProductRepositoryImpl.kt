package com.ssafy.locket.data.repository.graph

import android.util.Log
import com.ssafy.locket.data.network.api.ProductService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.graph.PriceHappinessResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.graph.ProductCategoryListResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.graph.ProductLikeListResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import com.ssafy.locket.model.graph.ProductHappyListInfo
import com.ssafy.locket.model.graph.ProductLikeListInfo
import com.ssafy.locket.model.graph.ProductxInfo
import com.ssafy.locket.repository.Product.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ProductRepositoryImpl @Inject constructor(
    private val productService: ProductService
): ProductRepository{
    override suspend fun addToFavorites(productId: Int): Flow<ResponseStatus<ProductxInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun addAlarm(productId: Int): Flow<ResponseStatus<ProductxInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCategoryList(category: Int, page: Int): Flow<ResponseStatus<ProductCategoryListInfo>> {
        return flow {
            val result = ApiResponseHandler().handle {
                productService.getCategoryList(category,page)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    Log.d("ProductFragment",result.data.toDomainModel().toString())
                    emit(ResponseStatus.Success(result.data.toDomainModel()))
                }
                is ApiResponse.Error -> {
                    val errorModel = result.error.toDomainModel()
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }

    override suspend fun getDetailProductInfo(
        productId: Int,
        userId: Int,
        size: Int
    ): Flow<ResponseStatus<ProductxInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLikeProductList(userId: Int): Flow<ResponseStatus<ProductLikeListInfo>> {
        return flow {
            val result = ApiResponseHandler().handle {
                productService.getLikeProductList(userId)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    Log.d("ProductFragment",result.data.toDomainModel().toString())
                    emit(ResponseStatus.Success(result.data.toDomainModel()))
                }
                is ApiResponse.Error -> {
                    val errorModel = result.error.toDomainModel()
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }

    override suspend fun getHappyProductList(): Flow<ResponseStatus<ProductHappyListInfo>> {
        return flow {
            val result = ApiResponseHandler().handle {
                productService.getHappiness()
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    Log.d("ProductFragment",result.data.toDomainModel().toString())
                    emit(ResponseStatus.Success(result.data.toDomainModel()))
                }
                is ApiResponse.Error -> {
                    val errorModel = result.error.toDomainModel()
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }
}