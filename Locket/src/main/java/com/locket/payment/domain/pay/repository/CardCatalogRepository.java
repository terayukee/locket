package com.locket.payment.domain.pay.repository;

import com.locket.payment.domain.pay.entity.CardCatalog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardCatalogRepository extends JpaRepository<CardCatalog, Integer> {
    List<CardCatalog> findByCompany_CompanyName(String companyName);
}
