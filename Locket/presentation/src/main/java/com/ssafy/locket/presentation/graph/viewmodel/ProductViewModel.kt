package com.ssafy.locket.presentation.graph.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.model.graph.ProductCategoryListInfo
import com.ssafy.locket.model.graph.ProductHappyListInfo
import com.ssafy.locket.model.graph.ProductLikeListInfo
import com.ssafy.locket.model.graph.ProductSearchInfo
import com.ssafy.locket.model.graph.product_detail.ProductDetailInfo
import com.ssafy.locket.usecase.product.ProductAlertUseCase
import com.ssafy.locket.usecase.product.ProductCategoryUseCase
import com.ssafy.locket.usecase.product.ProductDetailUseCase
import com.ssafy.locket.usecase.product.ProductHappyListUseCase
import com.ssafy.locket.usecase.product.ProductLikeClickUseCase
import com.ssafy.locket.usecase.product.ProductLikeListUseCase
import com.ssafy.locket.usecase.product.ProductSearchListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productCategoryUseCase: ProductCategoryUseCase,
    private val productLikeListUseCase: ProductLikeListUseCase,
    private val productHappyListUseCase: ProductHappyListUseCase,
    private val productDetailUseCase: ProductDetailUseCase,
    private val productLikeClickUseCase: ProductLikeClickUseCase,
    private val productAlertUseCase: ProductAlertUseCase,
    private val productSearchListUseCase: ProductSearchListUseCase
) : ViewModel() {
    private val _productCategoryInfo = MutableStateFlow<ProductCategoryListState>(ProductCategoryListState.Initial)
    val productCategoryInfo: StateFlow<ProductCategoryListState> = _productCategoryInfo.asStateFlow()

    private val _productLikeListInfo = MutableStateFlow<ProductLikeListState>(ProductLikeListState.Initial)
    val productLikeListInfo :StateFlow<ProductLikeListState> = _productLikeListInfo.asStateFlow()

    private val _productHappyListInfo = MutableStateFlow<ProductHappyListState>(ProductHappyListState.Initial)
    val productHappyListInfo :StateFlow<ProductHappyListState> = _productHappyListInfo.asStateFlow()

    private val _productDetailInfo = MutableStateFlow<ProductDetailState>(ProductDetailState.Initial)
    val productDetailInfo  :StateFlow<ProductDetailState> = _productDetailInfo .asStateFlow()

    private val _productLikeClickInfo = MutableStateFlow<ProductLikeClickState>(ProductLikeClickState.Initial)
    val productLikeClickInfo  :StateFlow<ProductLikeClickState> = _productLikeClickInfo.asStateFlow()

    private val _productAlertInfo = MutableStateFlow<ProductAlertState>(ProductAlertState.Initial)
    val productAlertInfo  :StateFlow<ProductAlertState> = _productAlertInfo.asStateFlow()

    private val _productSearchInfo = MutableStateFlow<ProductSearchState>(ProductSearchState.Initial)
    val productSearchInfo :StateFlow<ProductSearchState> = _productSearchInfo.asStateFlow()

    private val _productName = MutableStateFlow("")
    val productName: StateFlow<String> get() = _productName
    fun updateProductName(searchProduct: String) {
        _productName.value = searchProduct
    }

    fun productCategorySetLoading() {
        _productCategoryInfo.value = ProductCategoryListState.Loading
    }
    fun productLikeListSetLoading(){
        _productLikeListInfo.value = ProductLikeListState.Loading
    }
    fun productHappyListSetLoading(){
        _productHappyListInfo.value = ProductHappyListState.Loading
    }
    fun productDetailSetLoading(){
        _productDetailInfo.value = ProductDetailState.Loading
    }

    fun productLikeClickSetLoading(){
        _productLikeClickInfo.value = ProductLikeClickState.Loading
    }

    fun productAlertSetLoading(){
        _productAlertInfo.value = ProductAlertState.Loading
    }

    fun productSearchSetLoading(){
        _productSearchInfo.value = ProductSearchState.Loading
    }

    fun resetProductDetailState() {
        _productDetailInfo.value = ProductDetailState.Initial
    }

    fun getCategoryList(category: Int, page: Int) {
        viewModelScope.launch {
            productCategoryUseCase(category, page)
                .onStart { productCategorySetLoading() }
                .catch { e -> Log.e("ProductFragment", "Error fetching category list: ${e.message}") }
                .firstOrNull()
                ?.let { uiState ->
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _productCategoryInfo.value = ProductCategoryListState.Success(uiState.data)
                            Log.d("ProductFragment", "Product: ${_productCategoryInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _productCategoryInfo.value = ProductCategoryListState.Error(uiState.error.message)
                            Log.d("ProductFragment", "error: ${_productCategoryInfo.value}")
                        }
                    }
                }
        }
    }

    fun getLikeList(userId: Int) {
        viewModelScope.launch {
            productLikeListUseCase(userId)
                .onStart { productLikeListSetLoading() }
                .catch { e -> Log.e("ProductFragment", "Error fetching category list: ${e.message}") }
                .firstOrNull()
                ?.let { uiState ->
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _productLikeListInfo.value = ProductLikeListState.Success(uiState.data)
                            Log.d("ProductFragment", "Product: ${_productLikeListInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _productLikeListInfo.value = ProductLikeListState.Error(uiState.error.message)
                            Log.d("ProductFragment", "error: ${_productLikeListInfo.value}")
                        }
                    }
                }
        }
    }

    fun getHappyList() {
        viewModelScope.launch {
            productHappyListUseCase()
                .onStart { productHappyListSetLoading() }
                .catch { e -> Log.e("ProductFragment", "Error fetching category list: ${e.message}") }
                .firstOrNull()
                ?.let { uiState ->
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _productHappyListInfo.value = ProductHappyListState.Success(uiState.data)
                        }
                        is ResponseStatus.Error -> {
                            _productHappyListInfo.value = ProductHappyListState.Error(uiState.error.message)
                        }
                    }
                }
        }
    }

    fun getDetailInfo(productId: Int, userId: Int) {
        viewModelScope.launch {
            productDetailUseCase(productId, userId)
                .onStart { productDetailSetLoading() }
                .catch { e -> Log.e("ProductFragment", "Error fetching category list: ${e.message}") }
                .firstOrNull()
                ?.let { uiState ->
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _productDetailInfo.value = ProductDetailState.Success(uiState.data)
                            Log.d("ProductFragment", "Product: ${_productDetailInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _productDetailInfo.value = ProductDetailState.Error(uiState.error.message)
                            Log.d("ProductFragment", "error: ${_productDetailInfo.value}")
                        }
                    }
                }
        }
    }

    fun productLikeClick(productId: Int, isLiked: Boolean) {
        viewModelScope.launch {
            productLikeClickUseCase(productId, isLiked)
                .onStart { productLikeClickSetLoading() }
                .catch { e -> Log.e("ProductFragment", "Error fetching category list: ${e.message}") }
                .firstOrNull()
                ?.let { uiState ->
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _productLikeClickInfo.value = ProductLikeClickState.Success(Unit)
                            Log.d("ProductFragment", "Product: ${_productLikeClickInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _productLikeClickInfo.value = ProductLikeClickState.Error(uiState.error.message)
                            Log.d("ProductFragment", "error: ${_productLikeClickInfo.value}")
                        }
                    }
                }
        }
    }

    fun productAlert(productId: Int, isAlert: Boolean, alertPrice: Int) {
        viewModelScope.launch {
            productAlertUseCase(productId, isAlert, alertPrice)
                .onStart { productAlertSetLoading() }
                .catch { e -> Log.e("ProductFragment", "Error fetching category list: ${e.message}") }
                .firstOrNull()
                ?.let { uiState ->
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _productAlertInfo.value = ProductAlertState.Success(Unit)
                            Log.d("ProductFragment", "Product: ${_productAlertInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _productAlertInfo.value = ProductAlertState.Error(uiState.error.message)
                            Log.d("ProductFragment", "error: ${_productAlertInfo.value}")
                        }
                    }
                }
        }
    }

    fun productSearch(product_name: String, page: Int) {
        viewModelScope.launch {
            productSearchListUseCase(product_name, page)
                .onStart { productSearchSetLoading() }
                .catch { e -> Log.e("ProductFragment", "Error fetching category list: ${e.message}") }
                .firstOrNull()
                ?.let { uiState ->
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _productSearchInfo.value = ProductSearchState.Success(uiState.data)
                            Log.d("ProductFragment", "Product: ${_productSearchInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _productSearchInfo.value = ProductSearchState.Error(uiState.error.message)
                            Log.d("ProductFragment", "error: ${_productSearchInfo.value}")
                        }
                    }
                }
        }
    }

}

sealed class ProductInfoState {
    object Initial: ProductInfoState()
    object Loading: ProductInfoState()
    data class Success(val product: Product): ProductInfoState()
    data class Error(val message: String): ProductInfoState()
}

sealed class ProductCategoryListState {
    object Initial : ProductCategoryListState()
    object Loading : ProductCategoryListState()
    data class Success(val productCategoryList: ProductCategoryListInfo) : ProductCategoryListState()
    data class Error(val message: String) : ProductCategoryListState()
}

sealed class ProductLikeListState {
    object Initial : ProductLikeListState()
    object Loading : ProductLikeListState()
    data class Success(val productLikeList: ProductLikeListInfo) : ProductLikeListState()
    data class Error(val message: String) : ProductLikeListState()
}

sealed class ProductHappyListState {
    object Initial : ProductHappyListState ()
    object Loading : ProductHappyListState ()
    data class Success(val productHappyListInfo: ProductHappyListInfo) : ProductHappyListState ()
    data class Error(val message: String) : ProductHappyListState ()
}

sealed class ProductDetailState {
    object Initial : ProductDetailState ()
    object Loading : ProductDetailState ()
    data class Success(val productDetailInfo: ProductDetailInfo) : ProductDetailState ()
    data class Error(val message: String) : ProductDetailState ()
}

sealed class ProductLikeClickState {
    object Initial : ProductLikeClickState ()
    object Loading : ProductLikeClickState ()
    data class Success(val unit: Unit) : ProductLikeClickState ()
    data class Error(val message: String) : ProductLikeClickState ()
}

sealed class ProductAlertState {
    object Initial : ProductAlertState ()
    object Loading : ProductAlertState ()
    data class Success(val unit: Unit) :ProductAlertState ()
    data class Error(val message: String) : ProductAlertState ()
}

sealed class ProductSearchState {
    object Initial : ProductSearchState ()
    object Loading : ProductSearchState ()
    data class Success(val productSearchInfo: ProductSearchInfo) :ProductSearchState ()
    data class Error(val message: String) : ProductSearchState ()
}