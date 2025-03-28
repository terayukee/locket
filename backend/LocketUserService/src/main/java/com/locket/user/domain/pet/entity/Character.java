package com.locket.user.domain.pet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "character", indexes = {
        @Index(name = "idx_character_user_id", columnList = "user_id")
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Character {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "character_id")
    private Long characterId;

    @Column(name = "character_name", nullable = false)
    private String characterName;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "exp", nullable = false)
    private Integer exp;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "food_count", nullable = false)
    private Integer foodCount;

    @Column(name = "toy_available", nullable = false)
    private Boolean toyAvailable;

    @Column(name = "next_toy_available_time")
    private LocalDateTime nextToyAvailableTime;

    public void feedCharacter(int expGain) {
        if (this.foodCount <= 0) {
            throw new IllegalArgumentException("사료가 부족합니다.");
        }
        this.exp += expGain;
        this.foodCount -= 1;
    }

    public void playWithCharacter(int expGain, LocalDateTime nextToyAvailableTime) {
        this.exp += expGain;
        this.toyAvailable = false;
        this.nextToyAvailableTime = nextToyAvailableTime;
    }

    public void resetToyCooldown() {
        this.toyAvailable = true;
        this.nextToyAvailableTime = null;
    }

    public void resetCharacter(String newCharacterName) {
        this.characterName = Objects.requireNonNull(newCharacterName, "캐릭터 이름은 null일 수 없습니다");
        this.exp = 0;
        this.foodCount = 0;
        this.toyAvailable = true;
        this.nextToyAvailableTime = null;
        this.createdAt = LocalDateTime.now();
    }

    public void addFood(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("추가할 사료 수량은 0보다 커야 합니다.");
        }
        this.foodCount += amount;
    }

    public boolean getToyAvailable() {
        return this.toyAvailable;
    }

    public boolean isToyAvailableNow() {
        if (this.toyAvailable) {
            return true;
        }
        if (this.nextToyAvailableTime == null) {
            return true;
        }
        return LocalDateTime.now().isAfter(this.nextToyAvailableTime);
    }

    // 현재 장난감 사용 가능 여부 & DB 갱신
    public boolean checkAndUpdateToyAvailability() {
        boolean isAvailableNow = isToyAvailableNow();
        if (isAvailableNow && !this.toyAvailable) {
            this.toyAvailable = true;
            this.nextToyAvailableTime = null;
        }
        return isAvailableNow;
    }
}
