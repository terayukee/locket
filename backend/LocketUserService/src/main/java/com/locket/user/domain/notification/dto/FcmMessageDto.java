package com.locket.user.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmMessageDto {
    private String targetFcmToken;
    private String title;
    private String content;
    private String type;
}
