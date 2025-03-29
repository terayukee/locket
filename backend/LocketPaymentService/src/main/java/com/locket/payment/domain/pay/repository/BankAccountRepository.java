package com.locket.payment.domain.pay.repository;

import com.locket.payment.domain.pay.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Integer> {

    // 계좌 ID를 통해 계좌 정보를 조회
    Optional<BankAccount> findById(Integer accountId);

    // 🔁 기존 비관적 락은 제거
     @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<BankAccount> findByAccountId(Integer accountId);

    // ✅ 낙관적 락에서는 일반 조회 메서드 사용 가능
//    Optional<BankAccount> findByAccountId(Integer accountId);
}
