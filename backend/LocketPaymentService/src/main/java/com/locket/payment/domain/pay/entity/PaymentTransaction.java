package com.locket.payment.domain.pay.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class PaymentTransaction {
    @Id
    private String paymentTransactionId;

    private int accountId;
    private int buyerId;
    private int sellerId;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentTransactionStatus;

    private String paymentCategory;
    private String paymentMerchant;

    private LocalDateTime paymentTimestamp;
}

