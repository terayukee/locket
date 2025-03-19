package com.locket.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Kafka에서 결제 성공 이벤트를 수신할 DTO (엘라스틱 서치에 저장될 모든 정보 포함)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentSuccessEvent {
    private String transactionId;
    private int buyerId;
    private int sellerId;
    private String userJob;
    private String birthDate; // Redis에서 가져올 사용자 정보
    private BigDecimal totalAmount;
    private String currency;
    private String paymentCategory;
    private String paymentMerchant;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private List<OrderDetail> orders;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class OrderDetail {
        private String orderId;
        private String cardNumber;
        private BigDecimal amount;
        private String paymentOrderStatus;
    }
}
