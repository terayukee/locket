package com.locket.user.service.auth;

import com.locket.common.jwt.JwtUtil;
import com.locket.user.domain.auth.dto.KakaoUserInfoDto;
import com.locket.user.domain.auth.dto.LoginResponseDto;
import com.locket.user.domain.auth.dto.SignupRequest;
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
    @Transactional(readOnly = true)
    public User processKakaoLogin(KakaoUserInfoDto kakaoUserInfo) {
        // 기존 사용자 확인
        return userRepository.findByLoginId(kakaoUserInfo.getId()).orElse(null);
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

        // 4. paymentPassword 검증
        if (request.getPaymentPassword() != null) {
            int paymentPassword = request.getPaymentPassword();
            if (paymentPassword < 1000 || paymentPassword > 9999) {
                throw new IllegalArgumentException("결제 비밀번호는 4자리 숫자여야 합니다.");
            }

            // 4-1. 결제 비밀번호 일치 여부 검증
            if (request.getConfirmPaymentPassword() == null) {
                throw new IllegalArgumentException("결제 비밀번호 확인은 필수 입력값입니다.");
            }

            if (!request.getPaymentPassword().equals(request.getConfirmPaymentPassword())) {
                throw new IllegalArgumentException("결제 비밀번호가 일치하지 않습니다.");
            }
        }

        // 5. fingerprintRegistered 검증
        if (request.getFingerprintRegistered() == null) {
            throw new IllegalArgumentException("지문 등록 여부는 필수 입력값입니다.");
        }

        // 사용자 생성
        User newUser = User.builder()
                .loginId(request.getLoginId())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .birthYear(request.getBirthYear())
                .userJob(UserJob.valueOf(request.getUserJob()))
                .paymentPassword(request.getPaymentPassword())
                .fingerprintRegistered(request.getFingerprintRegistered())
                .createdAt(LocalDateTime.now())
                .isDeleted(false)
                .build();

        return userRepository.save(newUser);
    }

    // 로그인, JWT 토큰 발급
    public LoginResponseDto login(User user) {
        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getNickname());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        // DB에 리프레시 토큰 저장
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

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
}