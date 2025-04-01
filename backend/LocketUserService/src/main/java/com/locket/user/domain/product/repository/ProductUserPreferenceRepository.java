package com.locket.user.domain.product.repository;

import com.locket.user.domain.product.entity.ProductUserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductUserPreferenceRepository extends JpaRepository<ProductUserPreference, Long> {
    Optional<ProductUserPreference> findByProductIdAndUserId(Integer productId, Long userId);
}