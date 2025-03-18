package com.ssafy.locket.data.repository

import com.ssafy.locket.data.remote.api.PaymentService
import com.ssafy.locket.data.remote.api.ReceiptService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(private val paymentService: PaymentService){


}