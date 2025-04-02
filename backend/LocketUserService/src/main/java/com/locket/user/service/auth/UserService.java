package com.locket.user.service.auth;

import com.locket.common.jwt.JwtUtil;
import com.locket.user.domain.auth.dto.*;
import com.locket.user.domain.auth.entity.User;
import com.locket.user.domain.auth.entity.UserJob;
import com.locket.user.domain.auth.repository.UserRepository;
import com.locket.user.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final KakaoService kakaoService;
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
        log.info("카카오 로그인 처리: kakaoId={}", kakaoUserInfo.getId());

        User user = userRepository.findByKakaoIdAndIsDeletedFalse(kakaoUserInfo.getId()).orElse(null);
        if (user != null) {
            if (fcmToken != null && !fcmToken.isEmpty() && !fcmToken.equals(user.getFcmToken())) {
                log.info("FCM 토큰 업데이트: userId={}", user.getUserId());
                user.updateFcmToken(fcmToken);
                userRepository.save(user);
            }
            return user;
        }

        // 탈퇴된 사용자 확인
        User deletedUser = userRepository.findByKakaoId(kakaoUserInfo.getId())
                .filter(u -> u.getIsDeleted())
                .orElse(null);

        if (deletedUser != null) {
            log.info("탈퇴한 사용자 로그인 시도: kakaoId={}", kakaoUserInfo.getId());
        } else {
            log.info("신규 사용자 로그인 시도: kakaoId={}", kakaoUserInfo.getId());
        }

        return null;
    }

    @Transactional
    public User registerUser(SignupRequest request) {
        log.info("회원가입(또는 탈퇴계정 복구) 요청 처리 시작");

        KakaoUserInfoDto kakaoUserInfo = kakaoService.getUserInfo(request.getAccessToken());
        Long kakaoId = kakaoUserInfo.getId();

        // 계정이 존재하면 오류
        Optional<User> existingActiveUser = userRepository.findByKakaoIdAndIsDeletedFalse(kakaoId);
        if (existingActiveUser.isPresent()) {
            log.warn("이미 가입된 활성 사용자: kakaoId={}", kakaoId);
            throw new IllegalArgumentException("이미 가입된 사용자입니다.");
        }

        // 탈퇴된 사용자 복구
        Optional<User> deletedUser = userRepository.findByKakaoId(kakaoId)
                .filter(u -> u.getIsDeleted());
        if (deletedUser.isPresent()) {
            User user = deletedUser.get();
            log.info("탈퇴한 사용자 재가입(복구): userId={}, kakaoId={}", user.getUserId(), kakaoId);

            // 새 결제 비밀번호, 지문등록여부 입력
            validateRequiredFields(request);
            validateBirthYear(request.getBirthYear());
            validatePaymentPassword(request.getPaymentPassword());
            if (request.getFingerprintRegistered() == null) {
                throw new IllegalArgumentException("지문 등록 여부는 필수 입력값입니다.");
            }

            UserJob userJob = UserJob.fromString(request.getUserJob());


            user.update(kakaoUserInfo.getNickname(), request.getBirthYear(), userJob);
            user.updateFcmToken(request.getFcmToken());

            user.setPaymentPassword(request.getPaymentPassword());
            user.setFingerprintRegistered(request.getFingerprintRegistered());

            // 탈퇴 해제
            user.setIsDeleted(false);

            User savedUser = userRepository.save(user);
            log.info("탈퇴 사용자 계정 복구 완료: userId={}, kakaoId={}", savedUser.getUserId(), kakaoId);
            return savedUser;
        }

        // 완전히 새로운 가입
        validateRequiredFields(request);
        validateBirthYear(request.getBirthYear());
        validatePaymentPassword(request.getPaymentPassword());
        if (request.getFingerprintRegistered() == null) {
            throw new IllegalArgumentException("지문 등록 여부는 필수 입력값입니다.");
        }

        UserJob userJob = UserJob.fromString(request.getUserJob());

        User newUser = User.builder()
                .kakaoId(kakaoId)
                .nickname(kakaoUserInfo.getNickname())
                .birthYear(request.getBirthYear())
                .userJob(userJob)
                .paymentPassword(request.getPaymentPassword())
                .fingerprintRegistered(request.getFingerprintRegistered())
                .fcmToken(request.getFcmToken())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        try {
            User savedUser = userRepository.save(newUser);
            log.info("신규 회원가입 완료: userId={}, kakaoId={}", savedUser.getUserId(), kakaoId);
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            log.error("사용자 저장 중 데이터 무결성 위반: {}", e.getMessage());
            if (e.getMessage().contains("duplicate key") && e.getMessage().contains("users_pkey")) {
                throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다. 다시 시도해주세요.");
            }
            throw e;
        }
    }

    private void validateRequiredFields(SignupRequest request) {
        if (request.getAccessToken() == null || request.getAccessToken().isEmpty()) {
            throw new IllegalArgumentException("카카오 액세스 토큰은 필수 입력값입니다.");
        }
    }

    private void validateBirthYear(Integer birthYear) {
        int currentYear = LocalDate.now().getYear();
        if (birthYear == null || birthYear < 1930 || birthYear > currentYear) {
            throw new IllegalArgumentException("유효하지 않은 출생년도입니다. 1930년부터 " + currentYear + "년까지의 값을 입력해주세요.");
        }
    }

    private void validatePaymentPassword(Integer paymentPassword) {
        if (paymentPassword != null) {
            if (paymentPassword < 100000 || paymentPassword > 999999) {
                throw new IllegalArgumentException("결제 비밀번호는 6자리 숫자여야 합니다.");
            }
        }
    }

    // 로그인 시 JWT 발급 후 Redis에 저장
    public LoginResponseDto login(User user) {
        log.info("로그인 처리: userId={}", user.getUserId());

        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getNickname());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        Long userId = user.getUserId();
        String userIdKey = REDIS_USER_PREFIX + userId;


        redisTemplate.opsForValue().set(userIdKey + REDIS_PAYMENT_PASSWORD_SUFFIX,
                String.valueOf(user.getPaymentPassword()));
        redisTemplate.opsForValue().set(userIdKey + REDIS_FINGERPRINT_SUFFIX,
                String.valueOf(user.getFingerprintRegistered()));
        redisTemplate.opsForValue().set(userIdKey + REDIS_REFRESH_TOKEN_SUFFIX,
                refreshToken, 7, TimeUnit.DAYS);

        redisTemplate.opsForValue().set(userIdKey + REDIS_BIRTH_YEAR_SUFFIX,
                String.valueOf(user.getBirthYear()));
        redisTemplate.opsForValue().set(userIdKey + REDIS_USER_JOB_SUFFIX,
                String.valueOf(user.getUserJob()));

        log.info("로그인 완료 및 토큰 발급: userId={}", userId);

        return LoginResponseDto.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(false)
                .build();
    }

    public User findById(Long userId) {
        log.info("사용자 조회: userId={}", userId);

        return userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> {
                    log.warn("사용자를 찾을 수 없음: userId={}", userId);
                    return new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
                });
    }

    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {
        log.info("사용자 정보 업데이트 요청: userId={}", userId);

        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> {
                    log.warn("업데이트할 사용자를 찾을 수 없음: userId={}", userId);
                    return new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
                });

        if (request.getBirthYear() != null) {
            validateBirthYear(request.getBirthYear());
        }

        UserJob userJob = null;
        if (request.getUserJob() != null) {
            userJob = UserJob.fromString(request.getUserJob());
        }

        user.update(request.getNickname(), request.getBirthYear(), userJob);

        // 회원 정보 수정 시 결제비밀번호/지문도 수정할지 고민

        User updatedUser = userRepository.save(user);
        log.info("사용자 정보 업데이트 완료: userId={}", userId);
        return updatedUser;
    }

    @Transactional
    public void deleteUser(Long userId) {
        log.info("회원 탈퇴 요청: userId={}", userId);

        User user = userRepository.findByUserIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> {
                    log.warn("탈퇴할 사용자를 찾을 수 없음: userId={}", userId);
                    return new ResourceNotFoundException("사용자를 찾을 수 없습니다.");
                });

        // 결제 비밀번호, 지문 등록 여부 초기화
        user.setPaymentPassword(null);
        user.setFingerprintRegistered(false);

        user.markAsDeleted();
        userRepository.save(user);

        String userIdKey = REDIS_USER_PREFIX + userId;
        redisTemplate.delete(userIdKey + REDIS_REFRESH_TOKEN_SUFFIX);
        redisTemplate.delete(userIdKey + REDIS_PAYMENT_PASSWORD_SUFFIX);
        redisTemplate.delete(userIdKey + REDIS_FINGERPRINT_SUFFIX);
        redisTemplate.delete(userIdKey + REDIS_BIRTH_YEAR_SUFFIX);
        redisTemplate.delete(userIdKey + REDIS_USER_JOB_SUFFIX);

        log.info("회원 탈퇴 처리 완료 (논리적 삭제): userId={}", userId);
    }
}
