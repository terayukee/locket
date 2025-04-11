package com.ssafy.locket.usecase.product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductAlertUseCase @Inject constructor(
    private val productRepository : ProductRepository
){
    suspend operator fun invoke(productId: Int,isAlert: Boolean,alertPrice: Int): Flow<ResponseStatus<Unit>> {
        return productRepository.addAlarm(productId,isAlert,alertPrice)
    }
}
