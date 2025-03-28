package com.locket.user.domain.goal.repository;

import com.locket.user.domain.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Optional<Goal> findByUserIdAndGoalYearAndGoalMonth(Long userId, Integer goalYear, Integer goalMonth);
}
