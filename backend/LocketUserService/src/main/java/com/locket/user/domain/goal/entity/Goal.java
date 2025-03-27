package com.locket.user.domain.goal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "goals")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "goal_amount", nullable = false)
    private Integer goalAmount;

    @Column(name = "used_amount", nullable = false)
    private Integer usedAmount;

    @Column(name = "is_achieved", nullable = false)
    private Boolean isAchieved;

    @Column(name = "goal_year", nullable = false)
    private Integer goalYear;

    @Column(name = "goal_month", nullable = false)
    private Integer goalMonth;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // ✅ 사용 금액 업데이트
    public void updateUsedAmount(int updatedUsedAmount) {
        this.usedAmount = updatedUsedAmount;
        this.isAchieved = this.usedAmount >= this.goalAmount;
    }
}
