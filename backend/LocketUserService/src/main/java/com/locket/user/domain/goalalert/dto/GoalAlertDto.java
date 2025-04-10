package com.locket.user.domain.goalalert.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
