package com.locket.user.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String productName;

    private String imageUrl;

    private String currentPrice;

    private String discountRate;

    private String highestPrice;
    private String discountAmount;
    private String unitPrice;
    private String shippingType;
    private String reviewCount;
    private String reviewRating;
    private String coupangUrl;
    private Integer averagePrice;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;
}