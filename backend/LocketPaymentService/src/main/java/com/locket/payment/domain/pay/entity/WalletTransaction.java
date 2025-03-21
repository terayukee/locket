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
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @Column(nullable = false)
    private Integer walletId; // 판매자의 지갑 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_order_id", nullable = false)
    private PaymentOrder paymentOrder; // ✅ 결제 주문과의 연관관계 추가 (NOT NULL)

    @Column(nullable = false)
    private BigDecimal amount; // 거래 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType; // 입금(Deposit), 출금(Withdrawal)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WalletTransactionStatus status; // EXECUTING, SUCCESS, FAIL

    @Column(nullable = false)
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
}
