package com.ssafy.locket.data.repository

import com.ssafy.locket.data.remote.api.ProductService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(private val productService: ProductService){
}