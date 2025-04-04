package com.locket.user.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class UserAlertDto {
    private String type;         // "goal" 또는 "product"
    private Long alertId;
    private String message;
    private boolean isRead;
    private Integer alertPrice; // goal은 null일 수 있음
    private LocalDateTime createdAt;
    private String formattedDate; // 🆕 "2025년 4월 4일" 형태
}
