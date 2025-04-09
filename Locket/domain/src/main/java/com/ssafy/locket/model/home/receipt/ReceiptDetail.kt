package com.ssafy.locket.model.home.receipt

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReceiptDetail(
    val itemId: Long,
    val itemAmount: Int,
    var itemCategory: String?,
    val itemName: String,
    val itemQuantity: Int
): BaseModel