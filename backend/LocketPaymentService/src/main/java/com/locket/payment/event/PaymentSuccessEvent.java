package com.locket.payment.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Kafka에서 결제 성공 이벤트를 수신할 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentSuccessEvent {
    private String transactionId;
    private int accountId;
    private int buyerId;
    private int sellerId;
    private String paymentCategory;
    private String paymentMerchant;
}
