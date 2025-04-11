package com.locket.payment.domain.pay.repository;

import com.locket.payment.domain.pay.entity.CardBenefit;
import com.locket.payment.domain.pay.entity.CardCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardBenefitRepository extends JpaRepository<CardBenefit, Integer> {
    List<CardBenefit> findByCard(CardCatalog card);
}
