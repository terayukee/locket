package com.locket.user.domain.budget.repository;

import com.locket.user.domain.budget.entity.Goals;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoalsRepository extends JpaRepository<Goals, Integer> {

    // 특정 연/월에 해당하는 목표를 조회 (가장 최신 1건)
    @Query(value = "SELECT * FROM goals g " +
            "WHERE g.user_id = :userId " +
            "AND DATE_PART('year', g.created_at) = :year " +
            "AND DATE_PART('month', g.created_at) = :month " +
            "ORDER BY g.created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Goals> findTopByUserIdAndYearMonth(@Param("userId") long userId,
                                                @Param("year") int year,
                                                @Param("month") int month);
}
