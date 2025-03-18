package com.locket.payment.domain.pay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QrPaymentRequest {
    private int accountId;
    private int buyerId;
    private int sellerId;
    private String paymentCategory;
    private String paymentMerchant;
    private double amount;
}
