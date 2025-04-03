package com.locket.user.domain.product.dto;

import com.locket.user.domain.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "상품 간단 정보 DTO")
public class ProductSummaryDTO {
    // 상품 요약 정보 (목록에 표시될 개별 상품)

    @Schema(description = "상품 ID")
    private Integer productId;

    @Schema(description = "상품명")
    private String productName;

    @Schema(description = "이미지 URL")
    private String imageUrl;

    @Schema(description = "현재 판매가")
    private String currentPrice;

    @Schema(description = "할인율")
    private String discountRate;

    public static ProductSummaryDTO fromEntity(Product product) {
        return ProductSummaryDTO.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .imageUrl(product.getImageUrl())
                .currentPrice(product.getCurrentPrice())
                .discountRate(product.getDiscountRate())
                .build();
    }
}
