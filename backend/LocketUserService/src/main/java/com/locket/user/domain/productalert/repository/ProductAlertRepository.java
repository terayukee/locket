package com.locket.user.domain.productalert.repository;

import com.locket.user.domain.productalert.entity.ProductAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductAlertRepository extends JpaRepository<ProductAlert, Long> {
    List<ProductAlert> findByUserId(Long userId);
    Optional<ProductAlert> findByUserIdAndMessage(Long userId, String message);
}