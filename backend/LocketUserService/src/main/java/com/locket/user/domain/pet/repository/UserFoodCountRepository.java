package com.locket.user.domain.pet.repository;

import com.locket.user.domain.pet.entity.UserFoodCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserFoodCountRepository extends JpaRepository<UserFoodCount, Long> {
    Optional<UserFoodCount> findByUserId(Long userId);
}