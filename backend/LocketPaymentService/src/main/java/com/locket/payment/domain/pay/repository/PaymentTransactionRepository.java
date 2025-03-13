package com.locket.payment.domain.pay.repository;

import com.locket.payment.domain.pay.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, String> {
}
