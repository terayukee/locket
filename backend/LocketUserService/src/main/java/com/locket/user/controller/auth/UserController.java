package com.locket.user.controller.auth;

import com.locket.user.domain.auth.dto.*;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.exception.ResourceNotFoundException;
import com.locket.user.service.auth.KakaoService;
import com.locket.user.service.auth.UserService;
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
public class UserController {
    private final KakaoService kakaoService;
    private final UserService userService;

    // 카카오 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KakaoLoginRequest request) {
        // 1. 카카오 액세스 토큰으로 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoService.getUserInfo(request.getAccessToken());

        // 2. 사용자 확인 (기존 회원인지 확인)
        User user = userService.processKakaoLogin(kakaoUserInfo);

        // 3. 회원이 아닌 경우 회원가입 필요 응답
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "회원가입이 필요합니다");
            response.put("kakao_id", kakaoUserInfo.getId());
            response.put("nickname", kakaoUserInfo.getNickname());
            response.put("email", kakaoUserInfo.getEmail());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 4. 로그인 처리 및 JWT 발급
        LoginResponseDto loginResponse = userService.login(user);

        return ResponseEntity.ok(loginResponse);
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // 1. 회원가입 처리
        User newUser = userService.registerUser(request);

        // 2. 로그인 처리 및 JWT 발급
        LoginResponseDto loginResponse = userService.login(newUser);

        return ResponseEntity.ok(loginResponse);
    }

    // 특정 사용자 정보 조회 - JWT 필터에서 이미 권한 검증 수행함
    @GetMapping("/{user_id}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable("user_id") Long pathUserId) {
        User user = userService.findById(pathUserId);

        UserDto userDto = UserDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .birthYear(user.getBirthYear())
                .userJob(user.getUserJob().toString())
                .build();

        return ResponseEntity.ok(userDto);
    }
}