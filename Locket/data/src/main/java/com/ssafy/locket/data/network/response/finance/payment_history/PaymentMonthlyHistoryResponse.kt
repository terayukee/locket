package com.ssafy.locket.data.network.response.finance.payment_history

import com.google.gson.annotations.SerializedName
import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyHistoryResponse.Companion.toDomainModel
import com.ssafy.locket.data.network.response.finance.payment_history.PaymentMonthlyHistoryResponseItem.Companion.toDomainModel
import com.ssafy.locket.model.payment_history.PaymentMonthlyHistory
import kotlinx.parcelize.Parcelize

@Parcelize
data class PaymentMonthlyHistoryResponse(
    @SerializedName("payments") val list: List<PaymentMonthlyHistoryResponseItem>
): BaseResponse {
    companion object: DataMapper<PaymentMonthlyHistoryResponse, PaymentMonthlyHistory> {
        override fun PaymentMonthlyHistoryResponse.toDomainModel(): PaymentMonthlyHistory {
            return PaymentMonthlyHistory(
                list = this.list.map { it.toDomainModel() }?: emptyList()
            )
        }
    }
}
