package com.locket.user.domain.productalert.repository;

import com.locket.user.domain.productalert.entity.ProductAlert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAlertRepository extends JpaRepository<ProductAlert, Long> {
}