package com.locket.user.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewUserResponse {

    @Schema(example = "회원가입이 필요합니다", description = "메시지")
    private String message;

    @Schema(example = "홍길동", description = "카카오 닉네임")
    private String nickname;

    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "액세스 토큰")
    private String accessToken;

    @Schema(example = "true", description = "신규 회원 여부")
    private boolean isNewUser;

}