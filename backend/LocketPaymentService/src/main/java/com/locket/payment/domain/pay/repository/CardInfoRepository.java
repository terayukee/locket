package com.locket.payment.domain.pay.repository;

import com.locket.payment.domain.pay.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Integer> {

    Optional<CardInfo> findByCardNumber(String cardNumber);
}
