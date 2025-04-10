package com.locket.user.domain.product.dto;

import com.locket.user.domain.product.entity.PriceHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceHistoryResponseDTO {
    // 가격 히스토리 정보
    private String priceDate;
    private Integer highestPrice;
    private Integer lowestPrice;
    private Integer averagePrice;

    public static ProductPriceHistoryResponseDTO fromEntity(PriceHistory priceHistory) {
        return ProductPriceHistoryResponseDTO.builder()
                .priceDate(priceHistory.getPriceDate())
                .highestPrice(priceHistory.getHighestPrice())
                .lowestPrice(priceHistory.getLowestPrice())
                .averagePrice(priceHistory.getAveragePrice())
                .build();
    }
}