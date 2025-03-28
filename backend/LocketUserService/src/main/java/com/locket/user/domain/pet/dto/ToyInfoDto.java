package com.locket.user.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToyInfoDto {
    private boolean isAvailable;
    private LocalDateTime nextAvailableTime;
    private long remainingTimeMinutes;
}