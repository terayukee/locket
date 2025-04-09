package com.locket.user.domain.auth.repository;

import com.locket.user.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByKakaoIdAndIsDeletedFalse(long kakaoId);

    Optional<User> findByUserIdAndIsDeletedFalse(Long userId);

    // 카카오 ID 전체 조회 (isDeleted 여부 무관) -> 탈퇴 유저 판별
    Optional<User> findByKakaoId(long kakaoId);
}
