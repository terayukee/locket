package com.locket.payment.domain.pay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String cardNumber;      // 카드 결제이므로 카드 번호 필수
    private int buyerId;
    private int sellerId;
    private String paymentCategory;
    private String paymentMerchant;
    private BigDecimal amount;
    private String storeName;
}
