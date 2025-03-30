package com.locket.user.controller.auth;

import com.locket.user.domain.auth.dto.*;
import com.locket.user.domain.auth.entity.User;
import com.locket.common.jwt.JwtUtil;
import com.locket.user.exception.ResourceNotFoundException;
import com.locket.user.exception.UnauthorizedException;
import com.locket.user.service.auth.KakaoService;
import com.locket.user.service.auth.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User", description = "회원가입, 로그인, 정보 조회/수정/삭제 API")
public class UserController {
    private final KakaoService kakaoService;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    // 카카오 로그인
    @Operation(
            summary = "소셜로그인",
            description = "로그인합니다. 신규 회원인 경우 회원가입이 필요합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "회원가입 필요 (신규 사용자)")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KakaoLoginRequest request) {
        // 카카오 액세스 토큰으로 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoService.getUserInfo(request.getAccessToken());

        // 사용자 확인 (기존 회원인지 확인)
        User user = userService.processKakaoLogin(kakaoUserInfo, request.getFcmToken());

        // 회원이 아닌 경우 회원가입 필요
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "회원가입이 필요합니다");
            response.put("kakao_id", kakaoUserInfo.getId());
            response.put("nickname", kakaoUserInfo.getNickname());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // 로그인 처리 및 JWT 발급
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
        // 회원가입 처리
        User newUser = userService.registerUser(request);

        // 로그인 처리 및 JWT 발급
        LoginResponseDto loginResponse = userService.login(newUser);

        return ResponseEntity.ok(loginResponse);
    }

    // 특정 사용자 정보 조회
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

    @Operation(summary = "[테스트용] Redis에 사용자 정보 저장", description = "로그인 시 저장되는 정보를 Redis에 수동으로 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Redis 저장 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/test/redis/{user_id}")
    public ResponseEntity<Map<String, String>> saveUserInfoToRedis(@PathVariable("user_id") Long userId) {
        // 사용자 조회
        User user = userService.findById(userId);

        // Redis 저장
        redisTemplate.opsForValue().set("user:" + userId + ":paymentPassword", String.valueOf(user.getPaymentPassword()));
        redisTemplate.opsForValue().set("user:" + userId + ":fingerprintRegistered", String.valueOf(user.getFingerprintRegistered()));
        redisTemplate.opsForValue().set("user:" + userId + ":refreshToken", "dummy-refresh-token", 7, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("user:" + userId + ":birthYear", String.valueOf(user.getBirthYear()));
        redisTemplate.opsForValue().set("user:" + userId + ":userJob", String.valueOf(user.getUserJob()));

        Map<String, String> response = new HashMap<>();
        response.put("message", "✅ Redis에 사용자 정보 저장 완료");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "액세스 토큰 갱신",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하고, 필요 시 리프레시 토큰도 갱신합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰")
    })
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        try {
            // 리프레시 토큰 검증
            jwtUtil.validateRefreshToken(refreshToken);

            // 사용자 ID 추출
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);

            // Redis에 저장된 리프레시 토큰과 비교
            String storedRefreshToken = redisTemplate.opsForValue().get("user:" + userId + ":refreshToken");
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다. 다시 로그인해주세요.");
            }

            // 사용자 정보 조회
            User user = userService.findById(userId);
            if (user.getIsDeleted()) {
                throw new ResourceNotFoundException("탈퇴한 사용자입니다.");
            }

            // 새 액세스 토큰 발급
            String newAccessToken = jwtUtil.createAccessToken(userId, user.getNickname());

            // 리프레시 토큰 남은 기간 확인
            Long ttl = redisTemplate.getExpire("user:" + userId + ":refreshToken", TimeUnit.MILLISECONDS);

            // 응답 빌더 초기화
            TokenRefreshResponse.TokenRefreshResponseBuilder responseBuilder = TokenRefreshResponse.builder()
                    .userId(userId)
                    .accessToken(newAccessToken);

            // 리프레시 토큰 만료일이 다가오면 새로 발급(1일 미만)
            if (ttl != null && ttl < 24 * 60 * 60 * 1000) {
                String newRefreshToken = jwtUtil.createRefreshToken(userId);
                redisTemplate.opsForValue().set("user:" + userId + ":refreshToken", newRefreshToken, 7, TimeUnit.DAYS);

                responseBuilder
                        .refreshToken(newRefreshToken)
                        .message("액세스 토큰과 리프레시 토큰이 모두 갱신되었습니다.");
            } else {
                responseBuilder.message("액세스 토큰이 갱신되었습니다.");
            }

            return ResponseEntity.ok(responseBuilder.build());

        } catch (UnauthorizedException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException("토큰 갱신 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

}