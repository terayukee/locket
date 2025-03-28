package com.locket.user.domain.pet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpActionRequest {
    private String characterName;
    private String actionType; // "feed" 또는 "play"
}