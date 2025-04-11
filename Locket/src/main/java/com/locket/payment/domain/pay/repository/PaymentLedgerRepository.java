package com.locket.payment.domain.pay.repository;

import com.locket.payment.domain.pay.entity.PaymentLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentLedgerRepository extends JpaRepository<PaymentLedger, Integer> {
    // 필요에 따라 추가 메서드 정의 가능
}
