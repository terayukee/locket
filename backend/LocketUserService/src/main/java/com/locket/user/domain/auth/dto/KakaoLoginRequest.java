package com.locket.user.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KakaoLoginRequest {
    @Schema(example = "kakao_access_token_value", description = "카카오 로그인 후 받은 액세스 토큰")
    private String accessToken;

    @Schema(example = "fcm_token_value", description = "FCM 토큰")
    private String fcmToken;
}