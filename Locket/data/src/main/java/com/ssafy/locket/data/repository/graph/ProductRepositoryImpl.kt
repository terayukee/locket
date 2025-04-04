package com.ssafy.locket.data.repository.graph

import android.util.Log
import androidx.datastore.dataStore
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.api.ProductService
import com.ssafy.locket.data.network.common.ApiResponse
import com.ssafy.locket.data.network.common.ApiResponseHandler
import com.ssafy.locket.data.network.common.ErrorResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.request.product.ProductAlertRequest
import com.ssafy.locket.data.network.request.product.ProductLikeRequest
import com.ssafy.locket.data.network.response.graph.PriceHappinessResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.graph.ProductCategoryListResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.graph.ProductDetailResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.graph.ProductLikeListResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.graph.ProductSearchResponse.Companion.toDomainModel
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import com.ssafy.locket.model.graph.ProductHappyListInfo
import com.ssafy.locket.model.graph.ProductLikeListInfo
import com.ssafy.locket.model.graph.ProductSearchInfo
import com.ssafy.locket.model.graph.ProductxInfo
import com.ssafy.locket.model.graph.product_detail.ProductDetailInfo
import com.ssafy.locket.repository.Product.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class ProductRepositoryImpl @Inject constructor(
    private val productService: ProductService,
    private val dataStore: UserDataStoreSource
): ProductRepository{
    override suspend fun addToFavorites(productId: Int,isLiked : Boolean): Flow<ResponseStatus<Unit>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                productService.addToFavorites(productId, ProductLikeRequest(isLiked,userId.toInt()))
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    Log.d("ProductFragment",result.data.toString())
                    emit(ResponseStatus.Success(Unit))
                }
                is ApiResponse.Error -> {
                    val errorModel = result.error.toDomainModel()
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
    }

    override suspend fun addAlarm(productId: Int,isAlert: Boolean,alertPrice: Int): Flow<ResponseStatus<Unit>> {
        return flow {
            val result = ApiResponseHandler().handle {
                val userId = dataStore.userId.first() ?: -1
                productService.addAlarm(productId, ProductAlertRequest(alertPrice,isAlert, userId.toInt()))
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
                    Log.d("ProductFragment",result.data.toString())
                    emit(ResponseStatus.Success(Unit))
                }
                is ApiResponse.Error -> {
                    val errorModel = result.error.toDomainModel()
                    emit(ResponseStatus.Error(result.error.toDomainModel()))
                }
            }
        }
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
    ): Flow<ResponseStatus<ProductDetailInfo>> {
        return flow {
            val result = ApiResponseHandler().handle {
                productService.getDetailProductInfo(productId,userId)
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

    override suspend fun getSearchProductList(
        product_name: String,
        page: Int
    ): Flow<ResponseStatus<ProductSearchInfo>> {
        return flow {
            val result = ApiResponseHandler().handle {
                productService.getSearchProduct(product_name,page)
            }.first() // ✅ 첫 번째 값만 가져옴
            when (result) {
                is ApiResponse.Success -> {
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