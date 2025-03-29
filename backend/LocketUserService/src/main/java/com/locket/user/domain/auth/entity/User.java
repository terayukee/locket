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

    @Column(name = "kakao_id")
    private Long kakaoId;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "birth_year", nullable = false)
    private Integer birthYear;

    @Column(name = "user_job", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserJob userJob;

    @Column(name = "payment_password")
    private Integer paymentPassword;

    @Column(name = "fingerprint_registered", nullable = false)
    private Boolean fingerprintRegistered;

    @Column(name = "fcm_token", length = 255)
    private String fcmToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    // FCM 토큰 업데이트
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void update(String nickname, Integer birthYear, UserJob userJob) {
        if (nickname != null) this.nickname = nickname;
        if (birthYear != null) this.birthYear = birthYear;
        if (userJob != null) this.userJob = userJob;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }
}