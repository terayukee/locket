package com.locket.payment.domain.pay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentOrderId;

    @ManyToOne
    @JoinColumn(name = "payment_transaction_id", nullable = false)
    private PaymentTransaction paymentTransaction; // ✅ 관계 설정

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private CardInfo card; // ✅ 카드 정보 참조 추가

    @ManyToOne
    @JoinColumn(name = "buyer_account", nullable = false)
    private BankAccount buyerAccount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // ✅ BigDecimal로 변경

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentOrderStatus;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(PaymentStatus status) {
        this.paymentOrderStatus = status;
        this.updatedAt = LocalDateTime.now();
    }
}
