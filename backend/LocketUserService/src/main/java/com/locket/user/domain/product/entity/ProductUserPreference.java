package com.locket.user.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_user_preferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductUserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id")
    private Product product;

    private Long userId;

    private boolean isLiked;

    private boolean isAlert;

    private Integer alertPrice;
}