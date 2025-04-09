package com.locket.user.domain.product.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "product_user_preferences",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "userId"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Long userId;

    private boolean isLiked;

    private boolean isAlert;

    private Integer alertPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}