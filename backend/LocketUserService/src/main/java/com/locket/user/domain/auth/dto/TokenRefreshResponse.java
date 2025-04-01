package com.locket.user.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRefreshResponse {
    @Schema(example = "1", description = "사용자 ID")
    private Long userId;

    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "액세스 토큰")
    private String accessToken;

    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "리프레시 토큰")
    private String refreshToken;

    @Schema(example = "액세스 토큰이 갱신되었습니다.", description = "처리 결과 메시지")
    private String message;
}