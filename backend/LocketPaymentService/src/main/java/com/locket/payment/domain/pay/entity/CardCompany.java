package com.locket.payment.domain.pay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_companies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer companyId;

    @Column(nullable = false, unique = true)
    private String companyName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
