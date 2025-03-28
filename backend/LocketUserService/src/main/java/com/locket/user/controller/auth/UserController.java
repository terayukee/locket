package com.locket.user.controller.auth;

import com.locket.user.domain.auth.dto.*;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.exception.ResourceNotFoundException;
import com.locket.user.service.auth.KakaoService;
import com.locket.user.service.auth.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원가입, 로그인, 정보 조회/수정/삭제 API")
public class UserController {
    private final KakaoService kakaoService;
    private final UserService userService;

    // 카카오 로그인
    @Operation(summary = "소셜로그인", description = "로그인합니다. 신규 회원인 경우 회원가입이 필요합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "회원가입 필요 (신규 사용자)")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KakaoLoginRequest request) {
        // 1. 카카오 액세스 토큰으로 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoService.getUserInfo(request.getAccessToken());

        // 2. 사용자 확인 (기존 회원인지 확인)
        User user = userService.processKakaoLogin(kakaoUserInfo, request.getFcmToken());

        // 3. 회원이 아닌 경우 회원가입 필요 응답
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "회원가입이 필요합니다");
            response.put("kakao_id", kakaoUserInfo.getId());
            response.put("nickname", kakaoUserInfo.getNickname());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 4. 로그인 처리 및 JWT 발급
        LoginResponseDto loginResponse = userService.login(user);

        return ResponseEntity.ok(loginResponse);
    }

    // 회원가입 처리
    @Operation(summary = "회원가입", description = "로그인 후 신규 사용자인 경우 이 API를 통해 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공 및 자동 로그인 처리"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패)")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // 1. 회원가입 처리
        User newUser = userService.registerUser(request);

        // 2. 로그인 처리 및 JWT 발급
        LoginResponseDto loginResponse = userService.login(newUser);

        return ResponseEntity.ok(loginResponse);
    }

    // 특정 사용자 정보 조회 - JWT 필터에서 이미 권한 검증 수행함
    @Operation(summary = "회원 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/{user_id}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable("user_id") Long pathUserId) {
        User user = userService.findById(pathUserId);

        UserDto userDto = UserDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .birthYear(user.getBirthYear())
                .userJob(user.getUserJob().toString())
                .build();

        return ResponseEntity.ok(userDto);
    }

    // 사용자 정보 수정
    @Operation(summary = "회원 정보 수정", description = "특정 사용자의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효성 검사 실패)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/{user_id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("user_id") Long userId,
            @RequestBody UserUpdateRequest request) {

        // 사용자 정보 업데이트
        User updatedUser = userService.updateUser(userId, request);

        // DTO 변환
        UserDto userDto = UserDto.builder()
                .userId(updatedUser.getUserId())
                .nickname(updatedUser.getNickname())
                .birthYear(updatedUser.getBirthYear())
                .userJob(updatedUser.getUserJob().toString())
                .build();

        return ResponseEntity.ok(userDto);
    }

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴", description = "특정 사용자의 계정을 탈퇴 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/{user_id}")
    public ResponseEntity<?> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원 탈퇴가 완료되었습니다");

        return ResponseEntity.ok(response);
    }
}