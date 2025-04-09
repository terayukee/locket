package com.locket.user.domain.goalalert.repository;

import com.locket.user.domain.goalalert.entity.GoalAlert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoalAlertRepository extends JpaRepository<GoalAlert, Long> {

    Optional<GoalAlert> findByUserIdAndGoalIdAndMessage(Long userId, Long goalId, String message);

    boolean existsByUserIdAndGoalIdAndMessage(Long userId, Long goalId, String message);

    List<GoalAlert> findByUserId(Long userId);
}
