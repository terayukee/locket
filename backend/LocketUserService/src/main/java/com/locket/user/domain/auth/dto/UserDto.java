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
public class UserDto {
    // 사용자 데이터 전달

    @Schema(example = "1", description = "사용자 ID")
    private Long userId;

    @Schema(example = "홍길동", description = "사용자 닉네임")
    private String nickname;

    @Schema(example = "1990", description = "출생년도")
    private Integer birthYear;

    @Schema(example = "직장인", description = "직업")
    private String userJob;
}