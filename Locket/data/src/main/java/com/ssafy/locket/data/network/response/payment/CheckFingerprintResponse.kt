package com.ssafy.locket.data.network.response.payment

import com.ssafy.locket.data.network.common.BaseResponse
import com.ssafy.locket.data.network.mapper.DataMapper
import com.ssafy.locket.model.payment.CheckFingerprint
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckFingerprintResponse(
    val fingerprintRegistered: Boolean,
    val userId: Int
): BaseResponse {
    companion object: DataMapper<CheckFingerprintResponse, CheckFingerprint> {
        override fun CheckFingerprintResponse.toDomainModel(): CheckFingerprint {
            return CheckFingerprint(fingerprintRegistered, userId)
        }

    }
}