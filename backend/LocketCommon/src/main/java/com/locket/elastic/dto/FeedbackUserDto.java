package com.locket.elastic.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackUserDto {
    private Long userId;
    private String nickname;
    private Integer birthYear;
    private String userJob;
}
