package com.locket.user.domain.payment.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "payment_history") // ✅ Elasticsearch에 저장될 인덱스명
public class PaymentHistory {

    @Id  // ✅ Elasticsearch에서 반드시 필요한 ID 필드
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
    private List<OrderDetail> orders;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderDetail {
        private String orderId;
        private String cardNumber;
        private BigDecimal amount;
        private String paymentOrderStatus;
    }
}
