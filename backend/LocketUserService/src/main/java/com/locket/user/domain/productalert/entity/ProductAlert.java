package com.locket.user.domain.productalert.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "price_id", nullable = false)
    private Long priceId; // Product ID

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_alert", nullable = false)
    private Boolean isAlert;

    @Column(name = "alert_price", nullable = false)
    private Integer alertPrice;
}