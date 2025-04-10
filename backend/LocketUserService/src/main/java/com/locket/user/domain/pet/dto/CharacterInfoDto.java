package com.locket.user.domain.pet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer foodCount;
    private ToyInfoDto toy;
}