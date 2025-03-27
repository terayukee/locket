package com.locket.user.domain.goalalert.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalAlertDto {
    private Long notificationId;
    private Long userId;
    private Long goalId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
