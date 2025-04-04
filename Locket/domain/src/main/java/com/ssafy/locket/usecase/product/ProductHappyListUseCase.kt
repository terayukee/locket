package com.ssafy.locket.usecase.product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductHappyListInfo
import com.ssafy.locket.repository.Product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductHappyListUseCase @Inject constructor(
    private val productRepository : ProductRepository
){
    suspend operator fun invoke(): Flow<ResponseStatus<ProductHappyListInfo>> {
        return productRepository.getHappyProductList()
    }
}