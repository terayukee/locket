package com.locket.user.domain.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    // 회원가입 시 클라이언트에서 서버로 전달하는 가입 정보
    // 신규 사용자가 추가 정보를 입력하고 회원가입 요청 시

    @Schema(example = "kakao_access_token_value", description = "카카오 액세스 토큰")
    private String accessToken;

    @Schema(example = "1990", description = "사용자 입력 출생년도")
    private Integer birthYear;

    @Schema(example = "직장인", description = "사용자 직업 (무직/직장인/자영업자)")
    private String userJob;

    @Schema(example = "123456", description = "결제 비밀번호 (6자리)")
    private Integer paymentPassword;

    @Schema(example = "true", description = "지문 등록 여부")
    private Boolean fingerprintRegistered;

    @Schema(example = "fcm_token_value", description = "Firebase Cloud Messaging 토큰")
    private String fcmToken;
}