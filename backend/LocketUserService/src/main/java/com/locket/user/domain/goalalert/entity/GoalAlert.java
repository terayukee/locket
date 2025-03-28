package com.locket.user.domain.goalalert.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "goals_alerts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "goal_id", nullable = false)
    private Long goalId;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static GoalAlert create(Long userId, Long goalId, String message) {
        return GoalAlert.builder()
                .userId(userId)
                .goalId(goalId)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
