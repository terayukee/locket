package com.ssafy.locket.usecase.product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductLikeListInfo
import com.ssafy.locket.repository.Product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductLikeListUseCase @Inject constructor(
    private val productRepository : ProductRepository
){
    suspend operator fun invoke(userId: Int,page: Int): Flow<ResponseStatus<ProductLikeListInfo>> {
        return productRepository.getLikeProductList(userId,page)
    }
}
