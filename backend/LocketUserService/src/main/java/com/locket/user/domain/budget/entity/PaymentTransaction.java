package com.locket.user.domain.budget.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_transaction_id")
    private Integer paymentTransactionId;

    @Column(name = "card_id", nullable = false)
    private Integer cardId;

    @Column(name = "buyer_id", nullable = false)
    private Long buyerId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "payment_transaction_status", nullable = false)
    private String paymentTransactionStatus; // 예: 'SUCCESS', 'EXECUTING', 'FAIL'

    @Column(name = "payment_category", nullable = false)
    private String paymentCategory;

    @Column(name = "payment_merchant", nullable = false)
    private String paymentMerchant;

    @Column(name = "payment_timestamp", nullable = false)
    private LocalDateTime paymentTimestamp;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
