package com.locket.payment.domain.pay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentTransactionId;

    private int accountId;
    private int buyerId;
    private int sellerId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentTransactionStatus;

    private String paymentCategory;
    private String paymentMerchant;

    private LocalDateTime paymentTimestamp;

    public void updateStatus(PaymentStatus status) {
        this.paymentTransactionStatus = status;
    }
}
