package com.locket.payment.domain.pay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "card_benefits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer benefitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardCatalog card;

    private String item;
    private String benefitDetail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
