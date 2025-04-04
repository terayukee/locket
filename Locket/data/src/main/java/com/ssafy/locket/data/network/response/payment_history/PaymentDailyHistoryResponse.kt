package com.ssafy.locket.data.network.response.payment_history

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.payment_history.PaymentDailyHistoryResponseItem.Companion.toDomainModel
import com.ssafy.locket.model.payment_history.PaymentDailyHistory
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentDailyHistoryResponse(
    val list: List<PaymentDailyHistoryResponseItem>
): BaseResponse {
    companion object: DataMapper<PaymentDailyHistoryResponse, PaymentDailyHistory> {
        override fun PaymentDailyHistoryResponse.toDomainModel(): PaymentDailyHistory {
            return PaymentDailyHistory(
                list = this.list.map { it.toDomainModel() }?: emptyList()
            )
        }
    }
}