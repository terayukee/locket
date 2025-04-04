package com.locket.user.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacterDeleteResponseDto {
    private Long userId;
    private Boolean deleted;
}