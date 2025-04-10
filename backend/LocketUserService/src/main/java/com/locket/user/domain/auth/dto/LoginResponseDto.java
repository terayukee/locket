package com.locket.user.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    // 서버에서 로그인 성공 후 클라이언트에게 응답하는 데이터
    // 로그인 성공 시 JWT와 함께 사용자 기본 정보 반환

    @Schema(example = "1", description = "사용자 ID")
    private Long userId;

    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "액세스 토큰")
    private String accessToken;

    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "리프레시 토큰")
    private String refreshToken;

    @Schema(example = "false", description = "신규 회원 여부")
    private boolean isNewUser;

}