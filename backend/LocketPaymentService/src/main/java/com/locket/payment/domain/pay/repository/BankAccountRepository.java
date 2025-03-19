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

    // 카드와 연결된 계좌 정보를 가져오기 (card_info를 통해 참조)
    Optional<BankAccount> findByCardInfo_CardId(Integer cardId);

    // 동시성 제어 (비관적 락 적용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<BankAccount> findByAccountId(Integer accountId);
}
