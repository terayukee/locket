package com.locket.user.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodResponseDto {
    private Integer previousFoodCount;
    private Integer addedFoodCount;
    private Integer currentFoodCount;
}
