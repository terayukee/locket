package com.ssafy.locket.usecase.product

import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import com.ssafy.locket.repository.product.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductCategoryUseCase @Inject constructor(
private val productRepository : ProductRepository
){
    suspend operator fun invoke(category: Int, page: Int): Flow<ResponseStatus<ProductCategoryListInfo>> {
        return productRepository.getCategoryList(category,page)
    }
}