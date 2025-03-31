package com.locket.payment.domain.pay.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private int cardId;
    private long sellerId;
    private String paymentCategory;
    private String paymentMerchant;
    private BigDecimal amount;
    private String storeName;
    private String paymentKey; // ✅ 중복 방지 키 추가
}

