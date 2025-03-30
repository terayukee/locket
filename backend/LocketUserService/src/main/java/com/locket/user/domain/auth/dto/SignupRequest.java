package com.locket.user.domain.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    // 회원가입 시 클라이언트에서 서버로 전달하는 가입 정보
    // 신규 사용자가 추가 정보를 입력하고 회원가입 요청 시

    private long kakaoId;       // 카카오 ID
    private String nickname;      // 사용자 닉네임
    private Integer birthYear;    // 사용자 입력 출생년도
    private String userJob;       // 사용자 직업 (무직/직장인/자영업자)
    private Integer paymentPassword; // 결제 비밀번호
    private Boolean fingerprintRegistered; // 지문 등록 여부
    private String fcmToken;      // FCM 토큰
}