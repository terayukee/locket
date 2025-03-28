package com.locket.user.domain.auth.dto;

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

    private long id;
    private String nickname;
}
