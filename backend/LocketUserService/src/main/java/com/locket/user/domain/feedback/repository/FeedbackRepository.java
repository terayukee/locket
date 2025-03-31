package com.locket.user.domain.feedback.repository;

import com.locket.user.domain.feedback.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findByUserIdAndFeedbackYearAndFeedbackMonth(Long userId, int year, int month);
}
