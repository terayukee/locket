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
}
