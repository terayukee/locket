package com.locket.user.domain.feedback.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {
    private Long userId;
    private Long goalId;
    private String feedbackText;
    private int feedbackYear;
    private int feedbackMonth;
}
