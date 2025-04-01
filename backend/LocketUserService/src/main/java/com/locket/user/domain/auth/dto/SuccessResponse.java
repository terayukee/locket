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
public class SuccessResponse {
    @Schema(example = "회원 탈퇴가 완료되었습니다", description = "성공 메시지")
    private String message;
}