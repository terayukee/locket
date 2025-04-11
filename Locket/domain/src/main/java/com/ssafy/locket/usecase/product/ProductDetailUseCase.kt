package com.ssafy.locket.usecase.product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.product_detail.ProductDetailInfo
import com.ssafy.locket.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductDetailUseCase @Inject constructor(
    private val productRepository : ProductRepository
) {
    suspend operator fun invoke(productId: Int,userId: Int): Flow<ResponseStatus<ProductDetailInfo>> {
        return productRepository.getDetailProductInfo(productId,userId)
    }
}