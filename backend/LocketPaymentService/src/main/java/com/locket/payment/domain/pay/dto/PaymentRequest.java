package com.locket.payment.domain.pay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private int cardId;
    private int buyerId;
    private int sellerId;
    private String paymentCategory;
    private String paymentMerchant;
    private BigDecimal amount;
    private String storeName;
}
