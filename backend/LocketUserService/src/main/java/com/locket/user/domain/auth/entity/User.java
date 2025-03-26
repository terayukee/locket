package com.locket.user.domain.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_id", nullable = false, length = 100)
    private String loginId;  // 카카오 ID를 저장

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "birth_year", nullable = false)
    private Integer birthYear;

    @Column(name = "user_job", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserJob userJob;

    @Column(name = "payment_password")
    private Integer paymentPassword;

    @Column(name = "fingerprint_registered", nullable = false)
    private Boolean fingerprintRegistered;

    @Column(name = "refresh_token", length = 255)
    private String refreshToken;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;


    // 리프레시 토큰 업데이트 메서드
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // FCM 토큰 업데이트
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}