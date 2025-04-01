package com.locket.user.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetail {

    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    private String highestPrice;

    private String discountAmount;

    private String unitPrice;

    private String shippingType;

    private String reviewCount;

    private String reviewRating;

    private String coupangUrl;

    private Integer averagePrice;
}