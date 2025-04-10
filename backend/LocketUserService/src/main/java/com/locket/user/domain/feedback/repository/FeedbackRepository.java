package com.locket.user.domain.feedback.repository;

import com.locket.user.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Long userId);
    Optional<Feedback> findByUserIdAndFeedbackYearAndFeedbackMonth(Long userId, int year, int month);
}
