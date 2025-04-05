package com.ssafy.locket.data.network.mapper

import com.ssafy.locket.data.network.request.home.receipt.ProcessReceiptDetailRequest
import com.ssafy.locket.model.home.receipt.ReceiptDetail

class ReceiptDetailRequestDtoMapper : RequestDtoMapper<ReceiptDetail, ProcessReceiptDetailRequest> {
    override fun map(input: ReceiptDetail): ProcessReceiptDetailRequest {
        return ProcessReceiptDetailRequest(
            itemId = input.itemId,
            itemAmount = input.itemAmount,
            itemCategory = input.itemCategory,
            itemName = input.itemName,
            itemQuantity = input.itemQuantity
        )
    }
}