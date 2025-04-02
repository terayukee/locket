package com.locket.user.domain.product.repository;

import com.locket.user.domain.product.entity.ProductUserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductUserPreferenceRepository extends JpaRepository<ProductUserPreference, Long> {
    Optional<ProductUserPreference> findByProductIdAndUserId(Integer productId, Long userId);

    // 사용자가 찜한 상품 목록 조회
    List<ProductUserPreference> findByUserIdAndIsLikedTrue(Long userId);

    // 사용자가 찜한 상품 목록 조회 (페이지네이션 적용)
    Page<ProductUserPreference> findByUserIdAndIsLikedTrue(Long userId, Pageable pageable);

    // 전체 찜한 상품 수 조회
    long countByUserIdAndIsLikedTrue(Long userId);
}