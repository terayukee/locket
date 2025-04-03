package com.ssafy.locket.usecase.product

import android.text.BoringLayout
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductLikeListInfo
import com.ssafy.locket.repository.Product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductLikeClickUseCase @Inject constructor(private val productRepository : ProductRepository) {
    suspend operator fun invoke(productId: Int,isLiked: Boolean): Flow<ResponseStatus<Unit>> {
        return productRepository.addToFavorites(productId,isLiked)
    }
}
