package com.locket.user.domain.auth.dto;

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

    private Long userId;
    private String accessToken;
    private String refreshToken;

}