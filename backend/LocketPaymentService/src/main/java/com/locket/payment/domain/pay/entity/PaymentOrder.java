package com.locket.payment.domain.pay.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentOrderId;

    private Long paymentTransactionId;
    private double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentOrderStatus;

    public void updateStatus(PaymentStatus status) {
        this.paymentOrderStatus = status;
    }
}
