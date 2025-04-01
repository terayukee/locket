package com.locket.user.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @Schema(example = "홍길동", description = "수정할 닉네임")
    private String nickname;

    @Schema(example = "1990", description = "수정할 출생년도")
    private Integer birthYear;

    @Schema(example = "직장인", description = "수정할 직업")
    private String userJob;
}