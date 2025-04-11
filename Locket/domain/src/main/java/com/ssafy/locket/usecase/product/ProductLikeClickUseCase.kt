package com.ssafy.locket.usecase.product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductLikeClickUseCase @Inject constructor(private val productRepository : ProductRepository) {
    suspend operator fun invoke(productId: Int,isLiked: Boolean): Flow<ResponseStatus<Unit>> {
        return productRepository.addToFavorites(productId,isLiked)
    }
}
