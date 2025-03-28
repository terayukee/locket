package com.locket.user.domain.pet.repository;

import com.locket.user.domain.pet.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByUserId(Long userId);

    List<Reward> findTop50ByUserIdOrderByReceivedAtDesc(Long userId);
}