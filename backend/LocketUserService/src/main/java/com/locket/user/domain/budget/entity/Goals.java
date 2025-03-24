package com.locket.user.domain.budget.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "goals")
public class Goals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Integer goalId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;  // users.user_id (int)

    @Column(name = "goal_amount", nullable = false)
    private Integer goalAmount;  // 예산 목표 금액

    @Column(name = "is_achieved", nullable = false)
    private Boolean isAchieved;  // 기본값 false

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt; // DEFAULT CURRENT_TIMESTAMP
}
