package com.locket.payment.domain.pay.repository;

import com.locket.payment.domain.pay.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Integer> {

    // userId를 기준으로 Wallet 조회
    Optional<Wallet> findByUserId(Long userId);
}
