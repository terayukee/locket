package com.locket.user.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoUserInfoDto {
    // 카카오 API에서 가져온 사용자 정보를 담는 객체
    // 카카오 API 호출 후 사용자 정보를 서비스 레이어로 전달할 때

    @Schema(example = "12345678", description = "카카오 사용자 ID")
    private long id;

    @Schema(example = "홍길동", description = "카카오 사용자 닉네임")
    private String nickname;
}
