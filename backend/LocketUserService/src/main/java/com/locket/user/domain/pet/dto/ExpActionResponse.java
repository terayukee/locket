package com.locket.user.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpActionResponse {
    private String characterName;
    private Integer previousExp; // 이전 경험치
    private Integer expGained;   // 획득한 경험치
    private Integer currentExp;
    private Double expPercentage; // 다음 레벨까지의 경험치 진행 백분율
    private Integer level;
    private Boolean levelUp;
}