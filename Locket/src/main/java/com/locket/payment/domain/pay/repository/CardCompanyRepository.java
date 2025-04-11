package com.locket.payment.domain.pay.repository;

import com.locket.payment.domain.pay.entity.CardCompany;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardCompanyRepository extends JpaRepository<CardCompany, Integer> {
    boolean existsByCompanyName(String companyName);
}
