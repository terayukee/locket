package com.locket.user.domain.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistoryDto {
    private String transactionId;
    private int buyerId;
    private int sellerId;
    private String userJob;
    private String birthDate;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentCategory;
    private String paymentMerchant;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private List<OrderDetailDto> orders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetailDto {
        private String orderId;
        private String cardNumber;
        private BigDecimal amount;
        private String paymentOrderStatus;
    }
}
