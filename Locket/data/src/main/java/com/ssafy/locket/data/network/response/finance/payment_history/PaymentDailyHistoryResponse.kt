package com.ssafy.locket.data.network.response.finance.payment_history

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentDailyHistoryResponseItem.Companion.toDomainModel
import com.ssafy.locket.model.finance.payment_history.PaymentDailyHistory
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentDailyHistoryResponse(
    @SerializedName("payments")val list: List<PaymentDailyHistoryResponseItem>
): BaseResponse {
    companion object: DataMapper<PaymentDailyHistoryResponse, PaymentDailyHistory> {
        override fun PaymentDailyHistoryResponse.toDomainModel(): PaymentDailyHistory {
            return PaymentDailyHistory(
                list = this.list.map { it.toDomainModel() }?: emptyList()
            )
        }
    }
}