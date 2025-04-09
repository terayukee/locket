package com.locket.user.domain.pet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reward")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private Long rewardId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "reward_name", nullable = false)
    private String rewardName;

    @Column(name = "character_name")
    private String characterName;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;
}
