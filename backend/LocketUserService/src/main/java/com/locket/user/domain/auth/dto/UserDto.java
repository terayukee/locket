package com.locket.user.domain.auth.dto;
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

    private Long userId;
    private String nickname;
    private String email;
    private Integer birthYear;
    private String userJob;
}