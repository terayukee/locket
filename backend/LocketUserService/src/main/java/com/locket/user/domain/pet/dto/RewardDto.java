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
public class RewardDto {
    private Long rewardId;
    private String rewardName;
    private LocalDateTime receivedAt;
    private String imageUrl;
}