package com.ssafy.locket.usecase.product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductSearchInfo
import com.ssafy.locket.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductSearchListUseCase @Inject constructor(
    private val productRepository : ProductRepository
){  suspend operator fun invoke(product_name: String,page: Int): Flow<ResponseStatus<ProductSearchInfo>> {
     return productRepository.getSearchProductList(product_name,page)
    }
}

