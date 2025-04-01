package com.locket.user.domain.product.dto;

import com.locket.user.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryDTO {
    // 상품 요약 정보 (목록에 표시될 개별 상품)

    private Integer productId;
    private String productName;
    private String imageUrl;
    private String currentPrice;
    private String discountRate;

    public static ProductSummaryDTO fromEntity(Product product) {
        return ProductSummaryDTO.builder()
                .productId(product.getId())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .currentPrice(product.getCurrentPrice())
                .discountRate(product.getDiscountRate())
                .build();
    }
}
