package com.locket.user.domain.auth.repository;

import com.locket.user.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 로그인 ID(카카오 ID)로 사용자 찾기
    Optional<User> findByKakaoId(long kakaoId);

}