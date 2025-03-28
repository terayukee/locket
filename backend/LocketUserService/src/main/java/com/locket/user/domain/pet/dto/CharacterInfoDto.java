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
public class CharacterInfoDto {
    private Long characterId;
    private String characterName;
    private Long userId;
    private Integer level;
    private Integer exp;
    private Integer totalExpForNextLevel;
    private Double expPercentage;
    private LocalDateTime createdAt;
    private Integer foodCount;
    private ToyInfoDto toy;
}