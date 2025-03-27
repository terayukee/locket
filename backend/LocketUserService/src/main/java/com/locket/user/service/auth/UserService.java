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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 카카오 로그인
    @Transactional
    public User processKakaoLogin(KakaoUserInfoDto kakaoUserInfo, String fcmToken) {

        // 기존 사용자 확인 및 탈퇴 여부 확인
        User user = userRepository.findByLoginId(kakaoUserInfo.getId()).orElse(null);

        // 사용자가 존재하고 탈퇴하지 않았다면 FCM 토큰 업데이트 후 반환
        if (user != null && !user.getIsDeleted()) {
            // FCM 토큰 업데이트
            if (fcmToken != null && !fcmToken.isEmpty()) {
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

        userRepository.findByLoginId(request.getLoginId()).ifPresent(user -> {
            throw new IllegalArgumentException("이미 가입된 사용자입니다.");
        });

        // 1. 필수 필드 검증
        if (request.getLoginId() == null || request.getLoginId().isEmpty()) {
            throw new IllegalArgumentException("로그인 ID는 필수 입력값입니다.");
        }

        if (request.getNickname() == null || request.getNickname().isEmpty()) {
            throw new IllegalArgumentException("닉네임은 필수 입력값입니다.");
        }

        // 2. birthYear 검증
        int currentYear = LocalDate.now().getYear();
        if (request.getBirthYear() == null || request.getBirthYear() < 1930 || request.getBirthYear() > currentYear) {
            throw new IllegalArgumentException("유효하지 않은 출생년도입니다. 1930년부터 " + currentYear + "년까지의 값을 입력해주세요.");
        }

        // 3. UserJob 검증
        String userJob = request.getUserJob();
        try {
            UserJob.valueOf(userJob);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 직업입니다. 직장인, 무직, 자영업자 중 하나를 선택해주세요.");
        }

        // 4. 결제 비밀번호 검증
        validatePaymentPassword(request.getPaymentPassword(), request.getConfirmPaymentPassword());

        // 5. 지문 등록 여부 검증
        if (request.getFingerprintRegistered() == null) {
            throw new IllegalArgumentException("지문 등록 여부는 필수 입력값입니다.");
        }

        // 사용자 생성
        User newUser = User.builder()
                .loginId(request.getLoginId())
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

    private void validatePaymentPassword(Integer paymentPassword, Integer confirmPaymentPassword) {
        // 결제 비밀번호가 있는 경우에만 검증
        if (paymentPassword != null) {
            // 4자리 숫자 검증
            if (paymentPassword < 1000 || paymentPassword > 9999) {
                throw new IllegalArgumentException("결제 비밀번호는 4자리 숫자여야 합니다.");
            }

            // 확인 비밀번호 필수 입력 검증
            if (confirmPaymentPassword == null) {
                throw new IllegalArgumentException("결제 비밀번호 확인은 필수 입력값입니다.");
            }

            // 비밀번호 일치 여부 검증
            if (!paymentPassword.equals(confirmPaymentPassword)) {
                throw new IllegalArgumentException("결제 비밀번호가 일치하지 않습니다.");
            }
        }
    }

    // 로그인, JWT 토큰 발급
    public LoginResponseDto login(User user) {
        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getNickname());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        // Redis에 리프레시 토큰 저장
//        tokenService.saveRefreshToken(user.getUserId(), refreshToken);

        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // 사용자 ID로 사용자 조회
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    // 사용자 정보 수정
    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {
        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        // 탈퇴 사용자 확인
        if (user.getIsDeleted()) {
            throw new ResourceNotFoundException("탈퇴한 사용자입니다.");
        }

        // 새로운 값 또는 기존 값을 사용하여 업데이트된 엔티티 생성
        User updatedUser = User.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .nickname(request.getNickname() != null ? request.getNickname() : user.getNickname())
                .birthYear(request.getBirthYear() != null ? request.getBirthYear() : user.getBirthYear())
                .userJob(request.getUserJob() != null ? UserJob.valueOf(request.getUserJob()) : user.getUserJob())
                .paymentPassword(user.getPaymentPassword())
                .fingerprintRegistered(user.getFingerprintRegistered())
                .fcmToken(user.getFcmToken())
                .createdAt(user.getCreatedAt())
                .isDeleted(user.getIsDeleted())
                .build();

        // 출생년도 검증 (변경할 경우)
        if (request.getBirthYear() != null) {
            int currentYear = LocalDate.now().getYear();
            if (request.getBirthYear() < 1930 || request.getBirthYear() > currentYear) {
                throw new IllegalArgumentException("유효하지 않은 출생년도입니다. 1930년부터 " + currentYear + "년까지의 값을 입력해주세요.");
            }
        }

        // 사용자 직업 검증 (변경할 경우)
        if (request.getUserJob() != null) {
            try {
                UserJob.valueOf(request.getUserJob());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("유효하지 않은 직업입니다. 직장인, 무직, 자영업자 중 하나를 선택해주세요.");
            }
        }

        // 저장 및 반환
        return userRepository.save(updatedUser);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        User deletedUser = User.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .birthYear(user.getBirthYear())
                .userJob(user.getUserJob())
                .paymentPassword(user.getPaymentPassword())
                .fingerprintRegistered(user.getFingerprintRegistered())
                .fcmToken(user.getFcmToken())
                .createdAt(user.getCreatedAt())
                .isDeleted(true) // 탈퇴 처리
                .build();

        userRepository.save(deletedUser);
    }

}