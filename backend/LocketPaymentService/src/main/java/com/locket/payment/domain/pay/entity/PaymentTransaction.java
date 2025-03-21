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
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentTransactionId;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private CardInfo card; // ✅ 카드 엔티티와 관계 설정

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount account;

    @Column(nullable = false)
    private Integer buyerId;

    @Column(nullable = false)
    private Integer sellerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentTransactionStatus;

    @Column(nullable = false)
    private String paymentCategory;

    @Column(nullable = false)
    private String paymentMerchant;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount; // ✅ 결제 금액 (BigDecimal로 변경)

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

    // ✅ 상태 업데이트 시 updatedAt도 변경
    public void updateStatus(PaymentStatus status) {
        this.paymentTransactionStatus = status;
        this.updatedAt = LocalDateTime.now();
    }
}
