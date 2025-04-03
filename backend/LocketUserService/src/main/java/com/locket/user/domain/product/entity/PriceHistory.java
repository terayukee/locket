package com.locket.user.domain.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "product_price_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long priceId;

    @ManyToOne
    @JoinColumn(name = "id")
    private Product product;

    @Column(nullable = false)
    private String priceDate;

    @Column(nullable = false)
    private Integer highestPrice;

    @Column(nullable = false)
    private Integer lowestPrice;
}