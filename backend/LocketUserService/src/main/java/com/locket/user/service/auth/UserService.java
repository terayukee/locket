package com.locket.user.service.auth;

import com.locket.common.jwt.JwtUtil;
import com.locket.user.domain.auth.dto.KakaoUserInfoDto;
import com.locket.user.domain.auth.dto.LoginResponseDto;
import com.locket.user.domain.auth.dto.SignupRequest;
import com.locket.user.domain.auth.dto.UserUpdateRequest;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.entity.UserJob;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_USER_PREFIX = "user:";
    private static final String REDIS_PAYMENT_PASSWORD_SUFFIX = ":paymentPassword";
    private static final String REDIS_FINGERPRINT_SUFFIX = ":fingerprintRegistered";
    private static final String REDIS_REFRESH_TOKEN_SUFFIX = ":refreshToken";
    private static final String REDIS_BIRTH_YEAR_SUFFIX = ":birthYear";
    private static final String REDIS_USER_JOB_SUFFIX = ":userJob";

    @Transactional
    public User processKakaoLogin(KakaoUserInfoDto kakaoUserInfo, String fcmToken) {

        User user = userRepository.findByKakaoId(kakaoUserInfo.getId()).orElse(null);

        // 사용자가 존재하고 탈퇴하지 않았다면 FCM 토큰 업데이트 후 반환
        if (user != null && !user.getIsDeleted()) {
            // FCM 토큰 업데이트 (변경된 경우에만)
            if (fcmToken != null && !fcmToken.isEmpty() && !fcmToken.equals(user.getFcmToken())) {
                user.updateFcmToken(fcmToken);
                userRepository.save(user);
            }
            return user;
        }
        // 탈퇴한 사용자거나 없는 사용자면 null 반환
        return null;
    }

    // 회원가입
    @Transactional
    public User registerUser(SignupRequest request) {
        userRepository.findByKakaoId(request.getKakaoId()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 가입된 사용자입니다.");
        });

        validateRequiredFields(request);
        validateBirthYear(request.getBirthYear());
        validateUserJob(request.getUserJob());
        validatePaymentPassword(request.getPaymentPassword());

        if (request.getFingerprintRegistered() == null) {
            throw new IllegalArgumentException("지문 등록 여부는 필수 입력값입니다.");
        }

        // 사용자 생성
        User newUser = User.builder()
                .kakaoId(request.getKakaoId())
                .nickname(request.getNickname())
                .birthYear(request.getBirthYear())
                .userJob(UserJob.valueOf(request.getUserJob()))
                .paymentPassword(request.getPaymentPassword())
                .fingerprintRegistered(request.getFingerprintRegistered())
                .fcmToken(request.getFcmToken())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        return userRepository.save(newUser);
    }

    private void validateRequiredFields(SignupRequest request) {
        if (request.getKakaoId() == 0) {
            throw new IllegalArgumentException("카카오 ID는 필수 입력값입니다.");
        }

        if (request.getNickname() == null || request.getNickname().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수 입력값입니다.");
        }
    }

    private void validateBirthYear(Integer birthYear) {
        int currentYear = LocalDate.now().getYear();
        if (birthYear == null || birthYear < 1930 || birthYear > currentYear) {
            throw new IllegalArgumentException("유효하지 않은 출생년도입니다. 1930년부터 " + currentYear + "년까지의 값을 입력해주세요.");
        }
    }

    private UserJob validateUserJob(String userJobStr) {
        try {
            return UserJob.valueOf(userJobStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 직업입니다. 직장인, 무직, 자영업자 중 하나를 선택해주세요.");
        }
    }

    private void validatePaymentPassword(Integer paymentPassword) {

        if (paymentPassword != null) {
            if (paymentPassword < 1000 || paymentPassword > 9999) {
                throw new IllegalArgumentException("결제 비밀번호는 4자리 숫자여야 합니다.");
            }
        }
    }

    // 로그인, JWT 토큰 발급
    public LoginResponseDto login(User user) {
        // 1. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getNickname());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        Long userId = user.getUserId();
        String userIdKey = REDIS_USER_PREFIX + userId;

        // 2. Redis 저장
        redisTemplate.opsForValue().set(userIdKey + REDIS_PAYMENT_PASSWORD_SUFFIX,
                String.valueOf(user.getPaymentPassword()));
        redisTemplate.opsForValue().set(userIdKey + REDIS_FINGERPRINT_SUFFIX,
                String.valueOf(user.getFingerprintRegistered()));

        // Refresh Token 저장 (7일 TTL)
        redisTemplate.opsForValue().set(userIdKey + REDIS_REFRESH_TOKEN_SUFFIX,
                refreshToken, 7, TimeUnit.DAYS);

        // 엘라스틱서치 결제 트랜잭션 저장 시 활용할 데이터
        redisTemplate.opsForValue().set(userIdKey + REDIS_BIRTH_YEAR_SUFFIX,
                String.valueOf(user.getBirthYear()));
        redisTemplate.opsForValue().set(userIdKey + REDIS_USER_JOB_SUFFIX,
                String.valueOf(user.getUserJob()));

        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        if (user.getIsDeleted()) {
            throw new ResourceNotFoundException("탈퇴한 사용자입니다.");
        }

        // 유효성 검사
        if (request.getBirthYear() != null) {
            validateBirthYear(request.getBirthYear());
        }

        UserJob userJob = null;
        if (request.getUserJob() != null) {
            userJob = validateUserJob(request.getUserJob());
        }

        // 사용자 정보 업데이트
        user.update(request.getNickname(), request.getBirthYear(), userJob);

        return userRepository.save(user);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // soft delete
        user.markAsDeleted();
        userRepository.save(user);

        // Redis에서 사용자 데이터 정리
        String userIdKey = REDIS_USER_PREFIX + userId;
        redisTemplate.delete(userIdKey + REDIS_REFRESH_TOKEN_SUFFIX);
        redisTemplate.delete(userIdKey + REDIS_PAYMENT_PASSWORD_SUFFIX);
        redisTemplate.delete(userIdKey + REDIS_FINGERPRINT_SUFFIX);
        redisTemplate.delete(userIdKey + REDIS_BIRTH_YEAR_SUFFIX);
        redisTemplate.delete(userIdKey + REDIS_USER_JOB_SUFFIX);
    }

}