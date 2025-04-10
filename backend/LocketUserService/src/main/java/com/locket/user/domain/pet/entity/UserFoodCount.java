package com.locket.user.domain.pet.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "user_food_count")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFoodCount {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "food_count", nullable = false)
    private Integer foodCount;

    public void updateFoodCount(int count) {
        this.foodCount = count;
    }
}