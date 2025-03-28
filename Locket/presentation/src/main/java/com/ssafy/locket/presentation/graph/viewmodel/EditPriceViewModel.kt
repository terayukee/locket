package com.ssafy.locket.presentation.graph.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class EditPriceViewModel : ViewModel() {
    private val _editprice = MutableStateFlow("")
    val editprice: StateFlow<String> get() = _editprice
    fun updatePrice(newPrice: String) {
        _editprice.value = newPrice
    }
}
