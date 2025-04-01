package com.locket.user.controller.auth;

import com.locket.user.domain.auth.dto.*;
import com.locket.user.domain.auth.entity.User;
import com.locket.common.jwt.JwtUtil;
import com.locket.user.exception.ErrorResponse;
import com.locket.user.exception.ResourceNotFoundException;
import com.locket.user.exception.UnauthorizedException;
import com.locket.user.service.auth.KakaoService;
import com.locket.user.service.auth.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "\uD83D\uDE4BUser", description = "회원가입, 로그인, 정보 조회/수정/삭제 API")
public class UserController {

    private final KakaoService kakaoService;
    private final UserService userService;
    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "소셜로그인",
            description = "로그인합니다. 신규 회원인 경우 회원가입이 필요합니다.",
            security = {}
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "회원가입 필요 (신규 사용자)",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "회원가입이 필요합니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "요청한 리소스를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody KakaoLoginRequest request) {

        // 카카오 액세스 토큰으로 사용자 정보 가져오기
        KakaoUserInfoDto kakaoUserInfo = kakaoService.getUserInfo(request.getAccessToken());

        // 사용자 확인 (기존 회원인지 확인)
        User user = userService.processKakaoLogin(kakaoUserInfo, request.getFcmToken());

        // 회원이 아닌 경우 회원가입 필요
        if (user == null) {
            // 401 Unauthorized
            NewUserResponse newUserResponse = userService.createNewUserResponse(
                    kakaoUserInfo, request.getAccessToken());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(newUserResponse);
        }

        // 로그인 처리 및 JWT 발급
        LoginResponseDto loginResponse = userService.login(user);

        // 플래그 추가 (기존 회원)
        loginResponse = LoginResponseDto.builder()
                .userId(loginResponse.getUserId())
                .accessToken(loginResponse.getAccessToken())
                .refreshToken(loginResponse.getRefreshToken())
                .isNewUser(false)
                .build();

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "회원가입", description = "로그인 후 신규 사용자인 경우 이 API를 통해 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 성공 및 자동 로그인 처리",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "인증이 필요한 API입니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "요청한 리소스를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "요청한 리소스를 찾을 수 없습니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            )
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        // 회원가입 처리
        User newUser = userService.registerUser(request);

        // 로그인 처리 및 JWT 발급
        LoginResponseDto loginResponse = userService.login(newUser);

        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "회원 정보 조회", description = "특정 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "인증이 필요한 API입니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "사용자를 찾을 수 없습니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            )
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

    @Operation(summary = "회원 정보 수정", description = "특정 사용자의 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "사용자 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "인증이 필요한 API입니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "해당 유저를 찾을 수 없습니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            )
    })
    @PatchMapping("/{user_id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable("user_id") Long userId,
            @RequestBody UserUpdateRequest request
    ) {
        User updatedUser = userService.updateUser(userId, request);

        UserDto userDto = UserDto.builder()
                .userId(updatedUser.getUserId())
                .nickname(updatedUser.getNickname())
                .birthYear(updatedUser.getBirthYear())
                .userJob(updatedUser.getUserJob().toString())
                .build();

        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "회원 탈퇴", description = "특정 사용자의 계정을 탈퇴 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "인증이 필요한 API입니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "탈퇴 대상 사용자를 찾을 수 없습니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            )
    })
    @DeleteMapping("/{user_id}")
    public ResponseEntity<SuccessResponse> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUser(userId);

        SuccessResponse response = SuccessResponse.builder()
                .message("회원 탈퇴가 완료되었습니다")
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "[테스트용] Redis에 사용자 정보 저장",
            description = "로그인 시 저장되는 정보를 Redis에 수동으로 저장합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Redis 저장 성공",
                    content = @Content(schema = @Schema(implementation = SuccessResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "인증이 필요한 API입니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "사용자를 찾을 수 없습니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            )
    })
    @PostMapping("/test/redis/{user_id}")
    public ResponseEntity<SuccessResponse> saveUserInfoToRedis(@PathVariable("user_id") Long userId) {

        User user = userService.findById(userId);

        redisTemplate.opsForValue().set("user:" + userId + ":paymentPassword",
                String.valueOf(user.getPaymentPassword()));
        redisTemplate.opsForValue().set("user:" + userId + ":fingerprintRegistered",
                String.valueOf(user.getFingerprintRegistered()));
        redisTemplate.opsForValue().set("user:" + userId + ":refreshToken",
                "dummy-refresh-token", 7, TimeUnit.DAYS);
        redisTemplate.opsForValue().set("user:" + userId + ":birthYear",
                String.valueOf(user.getBirthYear()));
        redisTemplate.opsForValue().set("user:" + userId + ":userJob",
                String.valueOf(user.getUserJob()));

        SuccessResponse response = SuccessResponse.builder()
                .message("✅ Redis에 사용자 정보 저장 완료")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "액세스 토큰 갱신",
            description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급하고, 필요 시 리프레시 토큰도 갱신합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = TokenRefreshResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "유효하지 않은 리프레시 토큰",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 401,
                                      "error": "Unauthorized",
                                      "message": "리프레시 토큰이 유효하지 않습니다. 다시 로그인해주세요.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자를 찾을 수 없음",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "status": 404,
                                      "error": "Not Found",
                                      "message": "탈퇴한 사용자입니다.",
                                      "timestamp": "2025-04-01T12:34:56.789Z"
                                    }
                                """
                            )
                    )
            )
    })
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        try {
            // 리프레시 토큰 검증
            jwtUtil.validateRefreshToken(refreshToken);

            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String storedRefreshToken = redisTemplate.opsForValue().get("user:" + userId + ":refreshToken");

            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                // 401
                throw new UnauthorizedException("리프레시 토큰이 유효하지 않습니다. 다시 로그인해주세요.");
            }

            User user = userService.findById(userId);
            if (user.getIsDeleted()) {
                // 404
                throw new ResourceNotFoundException("탈퇴한 사용자입니다.");
            }

            // 새 액세스 토큰 발급
            String newAccessToken = jwtUtil.createAccessToken(userId, user.getNickname());
            Long ttl = redisTemplate.getExpire("user:" + userId + ":refreshToken", TimeUnit.MILLISECONDS);

            TokenRefreshResponse.TokenRefreshResponseBuilder builder = TokenRefreshResponse.builder()
                    .userId(userId)
                    .accessToken(newAccessToken);

            // 리프레시 토큰 남은 기간이 1일 미만이면 새로 발급
            if (ttl != null && ttl < 24 * 60 * 60 * 1000) {
                String newRefreshToken = jwtUtil.createRefreshToken(userId);
                redisTemplate.opsForValue().set("user:" + userId + ":refreshToken", newRefreshToken, 7, TimeUnit.DAYS);

                builder
                        .refreshToken(newRefreshToken)
                        .message("액세스 토큰과 리프레시 토큰이 모두 갱신되었습니다.");
            } else {
                builder.message("액세스 토큰이 갱신되었습니다.");
            }

            return ResponseEntity.ok(builder.build());
        } catch (UnauthorizedException | ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            // 401 처리
            throw new UnauthorizedException("토큰 갱신 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
